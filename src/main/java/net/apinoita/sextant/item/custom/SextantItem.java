package net.apinoita.sextant.item.custom;

import net.apinoita.sextant.util.ModCheckUtil;
import net.apinoita.sextant.util.ModMeasuringUtil;
import net.apinoita.sextant.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.apinoita.sextant.util.config.Configs;

import java.util.List;

public class SextantItem extends Item {

    public SextantItem(Settings settings) {
        super(settings);
    }

    public static final int MAX_USE_TIME = 1200;

    public static final int inventorySize = 2;
    private static final int insertSlot = 0;
    public static final int spyglassDecimals = 2;
    private final String os = System.getProperty("os.name");
    private int snappedCountdown;

    private final SimpleInventory inventory = new SimpleInventory(inventorySize) {
        @Override
        public int getMaxCountPerStack() {return 1;}

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {return stack.isOf(Items.COMPASS);}
                case 1 -> {return stack.isOf(Items.SPYGLASS);}
            }
            return true;
        }
    };

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack sextantStack = user.getStackInHand(hand);
        playStartUsingSound(user);
        if (!world.isClient()) {
            snappedCountdown = 0;
            invFromNBT(sextantStack);
            //checking which of two different actions should be performed
            if(user.isSneaking()){
                ItemStack insertStack = user.getInventory().getStack(insertSlot);
                if(insertStack.isOf(Items.COMPASS) || insertStack.isOf(Items.SPYGLASS)){insertItem(insertStack, user);}
                else{outputItem(user);}
                saveNbt(sextantStack, -1, -1);
            }
            else {
                startMeasuring(sextantStack, user.getHeadYaw());
            }
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity user){
            if (ModCheckUtil.itemInSextant(stack, Items.COMPASS) && selected && (Configs.clientConfig.snappingThreshold != 0f || Configs.clientConfig.snappingThresholdSpyglass != 0f)) {
                snapHeadYaw(user);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            float latestMeasurement = stack.getNbt().getFloat("sextant.sextant.latest_measurement");
            String angleText;
            int spyglassDecimalMultiplier = ModCheckUtil.itemInSextant(stack, Items.SPYGLASS) ? (int) Math.pow(10, spyglassDecimals) : 1;

            switch (Configs.clientConfig.angleUnit) {
                case RADIANS ->
                        angleText = Math.round(100 * spyglassDecimalMultiplier * (Math.round(latestMeasurement) * Math.PI / 180)) / (100 * spyglassDecimalMultiplier) + "rad";
                // default case is DEGREES
                default ->
                        angleText = Math.round(latestMeasurement * spyglassDecimalMultiplier) / spyglassDecimalMultiplier + "°";
            }
            tooltip.add(Text.translatable("item.sextant.tooltip.latest_measurement", angleText));
            tooltip.add(Text.translatable("item.sextant.tooltip.compass", inventory.getStack(0).isEmpty() ? "O" : "I"));
            tooltip.add(Text.translatable("item.sextant.tooltip.spyglass", inventory.getStack(1).isEmpty() ? "O" : "I"));
            tooltip.add(Text.translatable("item.sextant.tooltip.expand_tooltip", os.equals("Mac OS X") ? "cmd" : "ctrl"));
        }
        if(net.minecraft.client.gui.screen.Screen.hasControlDown()){
            tooltip.add(Text.translatable("item.sextant.tooltip.instructions.insert"));
            tooltip.add(Text.translatable("item.sextant.tooltip.instructions.output"));
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        playStopUsingSound(user);
        if (!world.isClient()) {stopMeasuring(stack, user.getHeadYaw());}
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        playStopUsingSound(user);
        if (!world.isClient()) {stopMeasuring(stack,user.getHeadYaw());}
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {return UseAction.SPYGLASS;}

    @Override
    public int getMaxUseTime(ItemStack stack) {return MAX_USE_TIME;}

    private void startMeasuring(ItemStack stack, float currentAngle){
        int spyglassDecimalMultiplier = ModCheckUtil.itemInSextant(stack, Items.SPYGLASS) ? (int) Math.pow(10, SextantItem.spyglassDecimals) : 1;
        float firstAngle = (((float) Math.round(ModMeasuringUtil.convertAngleTo360format(currentAngle) * spyglassDecimalMultiplier)) / spyglassDecimalMultiplier);
        float latestMeasurement;
        if (stack.hasNbt()){latestMeasurement = -1;}
        else{latestMeasurement = 0;}
        saveNbt(stack, firstAngle, latestMeasurement);
    }

    private void stopMeasuring(ItemStack stack, float currentAngle){
        float latestMeasurement;
        int spyglassDecimalMultiplier = ModCheckUtil.itemInSextant(stack, Items.SPYGLASS) ? (int) Math.pow(10, SextantItem.spyglassDecimals) : 1;
        if(stack.hasNbt()){
            latestMeasurement = ModMeasuringUtil.calculateMeasurement(stack.getNbt().getFloat("sextant.sextant.first_angle"),
                    (((float) Math.round(ModMeasuringUtil.convertAngleTo360format(currentAngle) * spyglassDecimalMultiplier)) / spyglassDecimalMultiplier));
        }
        else{latestMeasurement=0;}
        saveNbt(stack, -1, latestMeasurement);
    }

    private void insertItem(ItemStack insertStack, PlayerEntity player){
        for(int i = 0; i < inventorySize; i++) {
            if (inventory.getStack(i).isEmpty() && inventory.isValid(i, insertStack)) {
                    inventory.setStack(i, insertStack);
                    player.getInventory().removeStack(insertSlot);
                    break;
            }
        }
    }

    private void outputItem(PlayerEntity player){
        for(int i=0; i<inventorySize; i++){
            if (inventory.getStack(i).getCount() != 0) {
                player.getInventory().offerOrDrop(inventory.getStack(i));
                inventory.removeStack(i);
                break;
            }
        }
    }

    private void saveNbt(ItemStack stack, float firstAngle, float latestMeasurement){
        NbtCompound NbtData = new NbtCompound();

        if (firstAngle<0){NbtData.putFloat("sextant.sextant.first_angle", stack.getNbt().getFloat("sextant.sextant.first_angle"));}
        else {NbtData.putFloat("sextant.sextant.first_angle", firstAngle);}
        if (latestMeasurement<0){NbtData.putFloat("sextant.sextant.latest_measurement", stack.getNbt().getFloat("sextant.sextant.latest_measurement"));}
        else {NbtData.putFloat("sextant.sextant.latest_measurement", latestMeasurement);}
        NbtData.put("sextant.sextant.inventory", inventory.toNbtList());

        stack.setNbt(NbtData);
    }

    private void invFromNBT(ItemStack stack) {
        if (stack.hasNbt()) {
            if (stack.getNbt().getList("sextant.sextant.inventory",10) != null)
                inventory.readNbtList(stack.getNbt().getList("sextant.sextant.inventory", 10));
        }

        //putting the items into the correct order since the items get pushed around for some reason
        for(int i=0; i<inventorySize; i++){
            if (!inventory.isValid(i, inventory.getStack(i))){
                for(int ii=i; ii<inventorySize; ii++){
                    if (inventory.isValid(ii, inventory.getStack(i))){
                        inventory.setStack(ii, inventory.getStack(i));
                        inventory.removeStack(i);
                    }
                }
            }
        }
    }
    private void playStopUsingSound(LivingEntity user){
        if (user.isSneaking()){
            user.playSound(ModSounds.SEXTANT_INSERT_ITEM, 0.7f, 0.5f);
        }
        else{
            user.playSound(ModSounds.SEXTANT_STOP_USING, 0.7f, 1.0f);
        }
    }
    private void playStartUsingSound(LivingEntity user){
        if(!user.isSneaking()) {
            user.playSound(ModSounds.SEXTANT_START_USING, 2.0f, 1.0f);
        }
    }

    private void snapHeadYaw(LivingEntity user){
        if (snappedCountdown <= 0) {
            for (int i = 0; i<360; i+=90){
                if (ModCheckUtil.isUsingSextant(user)&&!user.isSneaking()){
                    if (ModCheckUtil.shouldSnap(ModMeasuringUtil.convertAngleTo360format(user.getHeadYaw()),i,ModCheckUtil.itemInSextant(user.getActiveItem(), Items.SPYGLASS))){
                        user.setYaw(i>180f?-(360f-i):i);
                        snappedCountdown = 31;
                    }
                }
            }
        }
        else if(snappedCountdown>20){
            user.setYaw(Math.round(ModMeasuringUtil.convertAngleTo360format(user.getHeadYaw())/90)*90);
            snappedCountdown--;
        }
        if (!(user.getHeadYaw() == 0f || user.getHeadYaw() == 90f|| user.getHeadYaw() == 180f|| user.getHeadYaw() == 270f|| user.getHeadYaw() == 360f)){
            snappedCountdown--;
        }
    }
}

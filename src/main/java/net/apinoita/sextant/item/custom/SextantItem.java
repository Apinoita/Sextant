package net.apinoita.sextant.item.custom;

import com.mojang.datafixers.kinds.IdF;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.apinoita.sextant.util.ModMeasuringUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.apinoita.sextant.util.config.Configs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SextantItem extends Item {

    public SextantItem(Settings settings) {
        super(settings);
    }

    public static final int MAX_USE_TIME = 1200;
    public static final float field_30922 = 0.1F;

    private final int inventorySize = 2;
    private final int insertSlot = 0;

    private final SimpleInventory inventory = new SimpleInventory(inventorySize) {
        @Override
        public int getMaxCountPerStack() {
            return 1; // Optional: customize the maximum count in each slot.
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            // Optional: restrict which stacks are allowed in which slot.
            switch (slot) {
                case 0 -> {
                    return stack.isOf(Items.COMPASS);
                }
                case 1 -> {
                    return stack.isOf(Items.SPYGLASS);
                }
            }
            return true;
        }
    };



    @Override
    public UseAction getUseAction(ItemStack stack) {
            return UseAction.SPYGLASS;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 1200;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            ItemStack sextantStack = user.getStackInHand(hand);
            invFromNBT(sextantStack);
            if(user.isSneaking()){
                Item insertItem = user.getInventory().getStack(insertSlot).getItem();
                if(insertItem==Items.COMPASS || insertItem==Items.SPYGLASS){insertItem(sextantStack, user);}
                else{outputItem(sextantStack, user);}
                saveNbt(sextantStack, -1, -1);
            }
            else {startMeasuring(sextantStack, Math.round(user.headYaw));}
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return ItemUsage.consumeHeldItem(world, user, hand);
    }



    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            stopMeasuring(stack, Math.round(user.headYaw));
        }
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient()) {
            stopMeasuring(stack, Math.round(user.headYaw));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()){
            float latestMeasurement = stack.getNbt().getFloat("sextant.sextant.latest_measurement");

            String angleText;

            switch(Configs.clientConfig.angleUnit){
                case RADIANS -> angleText = Math.round(100 * (Math.round(latestMeasurement) * Math.PI / 180)) /100 + "rad";
                // default case is DEGREES
                default -> angleText = Math.round(latestMeasurement) + "°";
            }

            tooltip.add(Text.translatable("item.sextant.tooltip.latest_measurement", angleText));
            tooltip.add(Text.translatable("item.sextant.tooltip.compass", inventory.getStack(0).isEmpty() ? "O":"I"));
            tooltip.add(Text.translatable("item.sextant.tooltip.spyglass", inventory.getStack(1).isEmpty() ? "O":"I"));
        }
    }

    private void startMeasuring(ItemStack stack, float currentAngle){

        float firstAngle = ModMeasuringUtil.convertAngleTo360format(currentAngle);
        float latestMeasurement;
        if (stack.hasNbt()){latestMeasurement = -1;}
        else{latestMeasurement = 0;}

        saveNbt(stack, firstAngle, latestMeasurement);
    }

    private void stopMeasuring(ItemStack stack, float currentAngle){

        float latestMeasurement;
        if(stack.hasNbt()){latestMeasurement = ModMeasuringUtil.calculateMeasurement(stack.getNbt().getFloat("sextant.sextant.first_angle"), ModMeasuringUtil.convertAngleTo360format(currentAngle));}
        else{latestMeasurement=0;}
        saveNbt(stack, -1, latestMeasurement);
    }

    private void insertItem(ItemStack stack, PlayerEntity player){
        ItemStack insertStack = player.getInventory().getStack(insertSlot);

        for(int i = 0; i < inventorySize; i++) {
            if (inventory.getStack(i).isEmpty() && inventory.isValid(i, insertStack)) {
                    inventory.setStack(i, insertStack);
                    player.getInventory().removeStack(insertSlot);
                    break;
            }
        }
    }

    private void outputItem(ItemStack stack, PlayerEntity player){
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

    public void invFromNBT(ItemStack stack) {
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
           /*
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            float yaw = user.headYaw;

            if(user.getStackInHand(hand).hasNbt()){

                if(user.getStackInHand(hand).getNbt().getBoolean("sextant.measuring")){stopMeasuring(user, hand, yaw);}

                else{startMeasuring(user, hand, yaw); user.sendMessage(Text.literal("measured"), false);}
            }
            else {startMeasuring(user, hand, yaw);}

            user.sendMessage(Text.literal("Current yaw " + yaw), false);
            }

        return super.use(world, user, hand);
    }
    */

    /*
    @Override

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            startMeasuring(user.getStackInHand(hand), user.headYaw);
            user.sendMessage(Text.literal("use"), false);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient()) {
            stopMeasuring(stack, user.headYaw);
            user.sendMessage(Text.literal("stop use"));
            super.onStoppedUsing(stack, world, user, remainingUseTicks);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient()) {
            stopMeasuring(stack, user.headYaw);
            user.sendMessage(Text.literal("finish use"));
        }
        return super.finishUsing(stack, world, user);
    }*/
}


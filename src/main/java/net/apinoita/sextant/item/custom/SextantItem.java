package net.apinoita.sextant.item.custom;

import me.fzzyhmstrs.fzzy_config.config.Config;
import net.apinoita.sextant.util.ModMeasuringUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
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
    public static final float field_30922 = 0.1F;


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
            startMeasuring(user.getStackInHand(hand), Math.round(user.headYaw));
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
            float latestMeasurement = stack.getNbt().getFloat("sextant.latest_measurement");
            String angledeg = Integer.toString(Math.round(latestMeasurement)) + "°";
            String anglerad = Float.toString((float) Math.round(100 * (Math.round(latestMeasurement) * Math.PI / 180)) /100) + "rad";

            switch(Configs.clientConfig.angleUnit){
                case DEGREES -> tooltip.add(Text.translatable("item.sextant.tooltip.latest_measurement", angledeg));
                case RADIANS -> tooltip.add(Text.translatable("item.sextant.tooltip.latest_measurement", anglerad));
            }
        }
    }

    private void startMeasuring(ItemStack stack, float currentAngle){

        NbtCompound NbtData = new NbtCompound();
        NbtData.putFloat("sextant.first_angle", ModMeasuringUtil.convertAngleTo360format(currentAngle));
        if (stack.hasNbt()){NbtData.putFloat("sextant.latest_measurement", stack.getNbt().getFloat("sextant.latest_measurement"));}
        else{NbtData.putFloat("sextant.latest_measurement",0F);}
        NbtData.putBoolean("sextant.measuring", true);
        stack.setNbt(NbtData);
    }

    private void stopMeasuring(ItemStack stack, float currentAngle){

        float latestMeasurement = ModMeasuringUtil.calculateMeasurement(stack.getNbt().getFloat("sextant.first_angle"), ModMeasuringUtil.convertAngleTo360format(currentAngle));

        NbtCompound NbtData = new NbtCompound();
        NbtData.putFloat("sextant.latest_measurement", latestMeasurement);
        NbtData.putFloat("sextant.first_angle", stack.getNbt().getFloat("sextant.first_angle"));
        NbtData.putBoolean("sextant.measuring", false);
        stack.setNbt(NbtData);
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


package net.apinoita.sextant.item.custom;

import net.apinoita.sextant.Sextant;
import net.apinoita.sextant.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class SextantItem extends Item {
    public SextantItem(Settings settings) {
        super(settings);
    }

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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()){
            float latestMeasurement = stack.getNbt().getFloat("sextant.latest_measurement");
            tooltip.add(Text.literal("Latest measurement " + latestMeasurement));
        }
    }

    private void startMeasuring(PlayerEntity user, Hand hand, float currentAngle){
        ItemStack sextant = user.getStackInHand(hand);

        NbtCompound NbtData = new NbtCompound();
        NbtData.putFloat("sextant.first_angle", convertAngleToFormatWithNoNegative(currentAngle));
        if (sextant.hasNbt()){NbtData.putFloat("sextant.latest_measurement", sextant.getNbt().getFloat("sextant.latest_measurement"));}
        else{NbtData.putFloat("sextant.latest_measurement",0F);}
        NbtData.putBoolean("sextant.measuring", true);
        sextant.setNbt(NbtData);
    }

    private void stopMeasuring(PlayerEntity user, Hand hand, float currentAngle){
        ItemStack sextant = user.getStackInHand(hand);

        float latestMeasurement = calculateMeasurement(sextant.getNbt().getFloat("sextant.first_angle"), convertAngleToFormatWithNoNegative(currentAngle));

        NbtCompound NbtData = new NbtCompound();
        NbtData.putFloat("sextant.latest_measurement", latestMeasurement);
        NbtData.putFloat("sextant.first_angle", -sextant.getNbt().getFloat("sextant.first_angle"));
        NbtData.putBoolean("sextant.measuring", false);
        sextant.setNbt(NbtData);
    }

    // 0/360(south) 270(east) 180(north) 90(west)
    private float convertAngleToFormatWithNoNegative(float angle){
        if (angle < 0){
            return 360F + angle;
        }
        return angle;
    }

    private float calculateMeasurement(float firstAngle, float secondAngle) {
        if (Math.abs(firstAngle - secondAngle) > 180F) {
            return Math.abs(secondAngle - firstAngle);
        } else {
            return Math.abs(firstAngle - secondAngle);
        }
    }
}


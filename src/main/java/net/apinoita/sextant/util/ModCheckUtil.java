package net.apinoita.sextant.util;

import net.apinoita.sextant.item.ModItems;
import net.apinoita.sextant.item.custom.SextantItem;
import net.apinoita.sextant.util.config.Configs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModCheckUtil {
    public static boolean isUsingSextant(LivingEntity user){
        return user.isUsingItem() && user.getActiveItem().isOf(ModItems.SEXTANT);
    }

    public static boolean itemInSextant(ItemStack itemStack, Item item){
        SimpleInventory inventory = new SimpleInventory(2);
        if (itemStack.hasNbt()){
            inventory.readNbtList(itemStack.getNbt().getList("sextant.sextant.inventory",10));
        }
        for (int i=0; i < SextantItem.inventorySize; i++){
            if (inventory.getStack(i).isOf(item)){
                return true;
            }
        }
        return false;
    }

    public static boolean shouldSnap(float value, float refValue, boolean spyglassEquipped){
        float threshold = spyglassEquipped?Configs.clientConfig.snappingThresholdSpyglass:Configs.clientConfig.snappingThreshold;
        if (refValue == 0){
            return value < 0+threshold || 360-threshold < value;
        }
        return value < refValue+threshold && refValue-threshold < value;
    }
}

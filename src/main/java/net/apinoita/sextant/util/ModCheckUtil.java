package net.apinoita.sextant.util;

import net.apinoita.sextant.item.ModItems;
import net.apinoita.sextant.item.custom.SextantItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModCheckUtil {
    public static boolean isUsingSextant(PlayerEntity player){
        return player.isUsingItem() && player.getActiveItem().isOf(ModItems.SEXTANT);
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
}

package net.apinoita.sextant.util;

import net.apinoita.sextant.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ModCheckUtil {
    public static boolean isUsingSextant(PlayerEntity player){
        return player.isUsingItem() && player.getActiveItem().isOf(ModItems.SEXTANT);
    }
}

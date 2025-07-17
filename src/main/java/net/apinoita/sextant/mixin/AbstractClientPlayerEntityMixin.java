package net.apinoita.sextant.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.apinoita.sextant.Sextant;
import net.apinoita.sextant.item.ModItems;
import net.apinoita.sextant.util.ModCheckUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    @WrapOperation(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float sextantCheckToGetFovMultiplier(float delta, float start, float end, Operation<Float> original){
         PlayerEntity player = MinecraftClient.getInstance().player;
        if (MinecraftClient.getInstance().player != null){
            if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && ModCheckUtil.isUsingSextant(player) && !player.isSneaking()) {
                if (ModCheckUtil.itemInSextant(player.getActiveItem(), Items.SPYGLASS)){
                    return 0.15f;
                }
                else{
                    return 0.8f;
                }
            }
        }
        return original.call(delta, start, end);
    }
}

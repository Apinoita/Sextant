package net.apinoita.sextant.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import net.apinoita.sextant.util.ModCheckUtil;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Final @Shadow
    private MinecraftClient client;
    @ModifyExpressionValue(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z")
    )
    private boolean isAlsoUsingSextant(boolean original) {
        if (client.player != null){
            return !(!original && !ModCheckUtil.isUsingSextant(client.player));
        }
        return !original;
    }

    /*@WrapOperation(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z")
    )
    private boolean isAlsoUsingSextant(AbstractClientPlayerEntity instance, boolean flag, Operation<Boolean> original) {
        if (ModCheckUtil.isUsingSextant(instance)) {
            return false;
        } else {
            return original.call(instance, flag);
        }
    }*/
}

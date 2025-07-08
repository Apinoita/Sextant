package net.apinoita.sextant.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.apinoita.sextant.util.ModCheckUtil;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @WrapOperation(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"))
    private boolean makeMouseSmoothWithSextant(ClientPlayerEntity instance, Operation<Boolean> original){
        return original.call(instance) || (ModCheckUtil.isUsingSextant(instance) && ModCheckUtil.itemInSextant(instance.getActiveItem(), Items.SPYGLASS));
    }
}

package net.apinoita.sextant.mixin;

import net.apinoita.sextant.util.ModCheckUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;
    //private float sextantScale;

    @Shadow private void renderOverlay(DrawContext context, Identifier texture, float opacity){}

    @Unique
    private static final Identifier SEXTANT_SCOPE = new Identifier("textures/misc/spyglass_scope.png");

    @Inject(method = "render", at = @At("HEAD"))
    private void InjectToRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (this.client.player != null) {
            if (ModCheckUtil.isUsingSextant(this.client.player)) {
                this.renderOverlay(context, SEXTANT_SCOPE, 1.0F);
            }
        }
    }
}

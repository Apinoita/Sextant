package net.apinoita.sextant.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.apinoita.sextant.util.ModCheckUtil;
import net.apinoita.sextant.util.ModMeasuringUtil;
import net.apinoita.sextant.util.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;


    @Shadow public TextRenderer getTextRenderer() {
        return null;
    }

    @Unique
    private static final Identifier SEXTANT_SCOPE = new Identifier("textures/misc/sextant_scope_1.png");

    @Inject(method = "render", at = @At("HEAD"))
    private void InjectToRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (this.client.player != null) {
            if (ModCheckUtil.isUsingSextant(this.client.player)) {
                if (this.client.options.getPerspective().isFirstPerson())
                {
                    //this.client.player.sendMessage(Text.literal("yaw: "+ ModMeasuringUtil.convertAngleTo360format(this.client.player.headYaw)));
                    if(this.client.player.getActiveItem().hasNbt()) {
                        renderSextantOverlay(context, ModMeasuringUtil.calculateMeasurement(this.client.player.getActiveItem().getNbt().getFloat("sextant.first_angle"), ModMeasuringUtil.convertAngleTo360format(this.client.player.headYaw)));
                    }
                }
            }
        }
    }

    @Unique
    private void renderSextantOverlay(DrawContext context, float angle_value){

        String angleString = "";
        switch(Configs.clientConfig.angleUnit){
            case DEGREES -> angleString = Integer.toString( Math.round(angle_value)) + "°";
            case RADIANS -> angleString = Float.toString((float) Math.round(100 * (Math.round(angle_value) * Math.PI / 180)) /100) + "rad";
        }
        float scale = 0.85F;
        float f = (float)Math.min(this.scaledWidth, this.scaledHeight);
        float h = Math.min((float)this.scaledWidth / f, (float)this.scaledHeight / f) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(f * h);
        int k = (this.scaledWidth - i) / 2;
        int l = (this.scaledHeight - j) / 25;
        int m = k + i;
        int n = l + j;
        RenderSystem.enableBlend();
        context.drawTexture(SEXTANT_SCOPE, k, l, -90, 0.0F, 0.0F, i, j, i, j);
        TextRenderer textRenderer = this.getTextRenderer();
        int textWidth = textRenderer.getWidth(angleString);

        context.fill(RenderLayer.getGuiOverlay(), 0, n, this.scaledWidth, this.scaledHeight, -90, 0xFF2b1f06);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.scaledWidth, l, -90, 0xFF2b1f06);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, 0xFF2b1f06);
        context.fill(RenderLayer.getGuiOverlay(), m, l, this.scaledWidth, n, -90, 0xFF2b1f06);

        //0xAARRGGBB
        context.drawTextWithShadow(textRenderer, angleString,(this.scaledWidth/2 - textWidth/2), this.scaledHeight- this.scaledHeight/8, 0xdbdbdb);

        /*context.fill(RenderLayer.getGuiOverlay(), 0, n, this.scaledWidth, this.scaledHeight, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.scaledWidth, l, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), m, l, this.scaledWidth, n, -90, -16777216);*/
    }
}

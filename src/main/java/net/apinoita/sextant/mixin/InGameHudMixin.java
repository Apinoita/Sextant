package net.apinoita.sextant.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import net.apinoita.sextant.Sextant;
import net.apinoita.sextant.item.custom.SextantItem;
import net.apinoita.sextant.util.ModCheckUtil;
import net.apinoita.sextant.util.ModMeasuringUtil;
import net.apinoita.sextant.util.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    @Unique
    private float sextantScale;

    @Shadow public TextRenderer getTextRenderer() {
        return null;
    }

    @Shadow private float spyglassScale;

    @Unique
    private static final Identifier SEXTANT_SCOPE = new Identifier(Sextant.MOD_ID, "textures/misc/sextant_scope_1.png");
    @Unique
    private static final Identifier COMPASS_DIRECTIONS = new Identifier(Sextant.MOD_ID, "textures/misc/compass_directions.png");
    @Unique
    private static final Identifier COMPASS_POINTER = new Identifier(Sextant.MOD_ID, "textures/misc/compass_pointer.png");


    @Inject(method = "render", at = @At("HEAD"))
    private void InjectToRenderSextantOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        float f = this.client.getLastFrameDuration();
        if (player != null) {
            ItemStack itemStack = player.getActiveItem();
            if (ModCheckUtil.isUsingSextant(player) && !player.isSneaking()) {
                this.sextantScale = MathHelper.lerp(0.4f * f, this.sextantScale, 0.8F);
                if (this.client.options.getPerspective().isFirstPerson()) {
                    if(itemStack.hasNbt()) {
                        float measurement = ModMeasuringUtil.calculateMeasurement(itemStack.getNbt().getFloat("sextant.sextant.first_angle"), ModMeasuringUtil.convertAngleTo360format(player.headYaw));
                        renderSextantOverlay(context, measurement, this.sextantScale, ModCheckUtil.itemInSextant(itemStack, Items.SPYGLASS), ModCheckUtil.itemInSextant(itemStack, Items.COMPASS), ModMeasuringUtil.convertAngleTo360format(player.headYaw));
                    }
                }
            }
            else{
                this.sextantScale = 0.5f;
            }
        }
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"))
    private boolean dontRenderHotbarWithSextantOverlay(InGameHud instance, float tickDelta, DrawContext context){
        if(this.client.player != null){
            return !ModCheckUtil.isUsingSextant(this.client.player) || this.client.player.isSneaking();
        }
        return true;
    }

    @Unique
    private void renderSextantOverlay(DrawContext context, float angle_value, float scale, boolean spyglassEquipped, boolean compassEquipped, float headYaw) {
        String angleString;
        int spyglassDecimalMultiplier = spyglassEquipped ? (int) Math.pow(10, SextantItem.spyglassDecimals) : 1;

        switch (Configs.clientConfig.angleUnit) {
            case RADIANS ->
                    angleString = Math.round(100 * spyglassDecimalMultiplier * (angle_value * Math.PI / 180)) / (100 * spyglassDecimalMultiplier) + "rad";
            // default case is DEGREES
            default -> {
                if (spyglassEquipped) {
                    angleString = (((float) Math.round(angle_value * spyglassDecimalMultiplier)) / spyglassDecimalMultiplier) + "°";
                } else {
                    angleString = Math.round(angle_value) + "°";
                }
            }
        }

        float f = (float) Math.min(this.scaledWidth, this.scaledHeight);
        float h = Math.min((float) this.scaledWidth / f, (float) this.scaledHeight / f) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(f * h);
        int k = (this.scaledWidth - i) / 2;
        int l = (2 * (this.scaledHeight - j)) / (compassEquipped?(7/2):5);
        int m = k + i;
        int n = l + j;
        RenderSystem.enableBlend();

        context.drawTexture(SEXTANT_SCOPE, k, l, -90, 0.0F, 0.0F, i, j, i, j);

        context.fill(RenderLayer.getGuiOverlay(), 0, n, this.scaledWidth, this.scaledHeight, -90, 0xFF2b1f06);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.scaledWidth, l, -90, 0xFF2b1f06);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, 0xFF2b1f06);
        context.fill(RenderLayer.getGuiOverlay(), m, l, this.scaledWidth, n, -90, 0xFF2b1f06);

        if(compassEquipped){
            int offset = scaledHeight/89;
            int pointerSize = scaledHeight/30;
            if (0 <= headYaw && headYaw <= 90) {
                context.drawTexture(COMPASS_DIRECTIONS, (Math.round((headYaw + 90) * 100) * scaledWidth) / (180 * 100) + offset, 0, 0, 0, scaledWidth * 2, scaledWidth / 16, scaledWidth * 2, scaledWidth / 16);
            }
            context.drawTexture(COMPASS_DIRECTIONS, (Math.round((headYaw + 90 - 360) * 100) * scaledWidth) / (180 * 100) + offset, 0, 0, 0, scaledWidth * 2, scaledWidth / 16, scaledWidth * 2, scaledWidth / 16);
            if (270 <= headYaw && headYaw <= 360) {
                context.drawTexture(COMPASS_DIRECTIONS, (Math.round((headYaw + 90 - 360 * 2) * 100) * scaledWidth) / (180 * 100) + offset, 0, 0, 0, scaledWidth * 2, scaledWidth / 16, scaledWidth * 2, scaledWidth / 16);
            }
            context.drawTexture(COMPASS_POINTER, scaledWidth/2-(pointerSize/2), scaledHeight/10, 0, 0, pointerSize, pointerSize, pointerSize, pointerSize);
        }


        TextRenderer textRenderer = this.getTextRenderer();
        int textWidth = textRenderer.getWidth(angleString);
        //0xAARRGGBB
        context.drawTextWithShadow(textRenderer, angleString,(this.scaledWidth/2 - textWidth/2), this.scaledHeight- this.scaledHeight/(compassEquipped?20:10), 0xdbdbdb);
    }
}

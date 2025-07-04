package net.apinoita.sextant.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.apinoita.sextant.item.ModItems;
import net.apinoita.sextant.util.ModCheckUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerHeldItemFeatureRenderer.class)
public class PlayerHeldItemFeatureRendererMixin <T extends PlayerEntity, M extends EntityModel<T> & ModelWithArms & ModelWithHead>
        extends HeldItemFeatureRenderer<T, M> {

    @Shadow @Final
    private HeldItemRenderer playerHeldItemRenderer;

    public PlayerHeldItemFeatureRendererMixin(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {super(context, heldItemRenderer);}

    @Unique
    private void renderSextant(LivingEntity entity, ItemStack stack, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        ModelPart modelPart = this.getContextModel().getHead();
        float f = modelPart.pitch;
        modelPart.pitch = MathHelper.clamp(modelPart.pitch, (float) (-Math.PI / 6F), (float) (Math.PI / 2));
        modelPart.rotate(matrices);
        modelPart.pitch = f;
        HeadFeatureRenderer.translate(matrices, false);
        boolean bl = (arm == Arm.LEFT);
        //matrices.translate((bl ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
        matrices.translate((bl ? -2.5F : 2.5F) / -50.0F, 0F, 0.0F);
        this.playerHeldItemRenderer.renderItem(entity, stack, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light);
        matrices.pop();
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    protected void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (stack.isOf(ModItems.SEXTANT) && entity.getActiveItem() == stack && entity.handSwingTicks == 0) {
            this.renderSextant(entity, stack, arm, matrices, vertexConsumers, light);
        }
    }


    @WrapWithCondition(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
        private boolean dontRenderItemWhenUsingSextant(HeldItemFeatureRenderer instance, LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.isPlayer()){
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null){return !ModCheckUtil.isUsingSextant(player) || player.isSneaking();}
        }
        return false;
    }
}

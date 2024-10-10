package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.extension.minecraft.OverlayRendered;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {
    @Shadow
    public static int getOverlayCoords(LivingEntityRenderState livingEntityRenderState, float f) {
        return 0;
    }

    @Shadow
    protected abstract float getWhiteOverlayProgress(S livingEntityRenderState);

    @Shadow
    protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Unique
    private BooleanOption showOwnNameInThirdPerson;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(EntityRendererProvider.Context context, M entityModel, float f, CallbackInfo ci) {
        showOwnNameInThirdPerson = CookeyMod.getInstance().getConfig().misc().showOwnNameInThirdPerson();
    }

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    public void showOwnName(T livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity == Minecraft.getInstance().cameraEntity
                && showOwnNameInThirdPerson.get()) cir.setReturnValue(true);
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V", ordinal = 0))
    public void renderWithOverlay(S livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci, @Local RenderLayer<S, M> renderLayer) {
        if (!(renderLayer instanceof OverlayRendered overlayRendered)) {
            return;
        }

        int overlayCoords = getOverlayCoords(livingEntityRenderState, this.getWhiteOverlayProgress(livingEntityRenderState));
        overlayRendered.cookeyMod$setOverlayCoords(overlayCoords);
    }
}

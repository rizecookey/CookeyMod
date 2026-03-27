package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    private BooleanOption disableCameraBobbing = CookeyMod.getInstance().getConfig().animations().disableCameraBobbing();

    @Unique
    private BooleanOption alternativeBobbing = CookeyMod.getInstance().getConfig().hudRendering().alternativeBobbing();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        disableCameraBobbing = modConfig.animations().disableCameraBobbing();
        alternativeBobbing = modConfig.hudRendering().alternativeBobbing();
    }

    @Redirect(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/OptionsRenderState;bobView:Z", opcode = Opcodes.GETFIELD),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/OptionsRenderState;screenEffectScale:F", opcode = Opcodes.GETFIELD)
            )
    )
    private boolean modifyBobViewInRenderLevel(OptionsRenderState instance) {
        return instance.bobView && !disableCameraBobbing.get();
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void changeToAlternativeBob(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
        if (!alternativeBobbing.get()) {
            return;
        }

        this.alternativeBobView(cameraState, poseStack);
        ci.cancel();
    }


    @Unique
    private void alternativeBobView(CameraRenderState cameraState, PoseStack poseStack) {
        if (!cameraState.entityRenderState.isPlayer) {
            return;
        }

        float backwardsInterpolatedWalkDistance = cameraState.entityRenderState.backwardsInterpolatedWalkDistance;
        float bob = cameraState.entityRenderState.bob;
        poseStack.translate(Mth.sin(backwardsInterpolatedWalkDistance * 3.1415927F) * bob * 0.5F, -Math.abs(Mth.cos(backwardsInterpolatedWalkDistance * 3.1415927F) * bob), 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.cos(backwardsInterpolatedWalkDistance * 3.1415927F) * bob * 3.0F));
    }
}

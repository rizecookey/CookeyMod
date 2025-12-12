package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> extends EntityModel<T> implements ArmedModel, HeadedModel {
    @Final
    @Shadow
    public ModelPart rightArm;

    @Final
    @Shadow
    public ModelPart leftArm;

    @Shadow
    protected abstract void poseLeftArm(T humanoidRenderState);

    @Shadow
    protected abstract void poseRightArm(T humanoidRenderState);

    protected HumanoidModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Unique
    private BooleanOption showEatingInThirdPerson;

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        showEatingInThirdPerson = modConfig.animations().showEatingInThirdPerson();
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;poseRightArm(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", ordinal = 0))
    private void potentiallyPoseOtherArm(T humanoidRenderState, CallbackInfo ci) {
        if (humanoidRenderState.cookeyMod$shouldPoseBothArms()) {
            poseLeftArm(humanoidRenderState);
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;poseLeftArm(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", ordinal = 0))
    private void potentiallyPoseOtherArm2(T humanoidRenderState, CallbackInfo ci) {
        if (humanoidRenderState.cookeyMod$shouldPoseBothArms()) {
            poseRightArm(humanoidRenderState);
        }
    }

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public void addRightArmAnimations(T humanoidRenderState, CallbackInfo ci) {
        HumanoidArm usedHand = humanoidRenderState.cookeyMod$getUsedArm();
        if (showEatingInThirdPerson.get()
                && humanoidRenderState.isUsingItem && usedHand == HumanoidArm.RIGHT && humanoidRenderState.cookeyMod$itemUseIsEating()) {
            boolean run = this.applyEatingAnimation(humanoidRenderState, usedHand, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
            if (run) ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public void addLeftArmAnimations(T humanoidRenderState, CallbackInfo ci) {
        HumanoidArm usedHand = humanoidRenderState.cookeyMod$getUsedArm();
        if (showEatingInThirdPerson.get()
                && humanoidRenderState.isUsingItem && usedHand == HumanoidArm.LEFT && humanoidRenderState.cookeyMod$itemUseIsEating()) {
            boolean run = this.applyEatingAnimation(humanoidRenderState, usedHand, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
            if (run) ci.cancel();
        }
    }

    // Animation values and "formula" from ItemInHandRenderer's applyEatAnimation

    @Unique
    public boolean applyEatingAnimation(T humanoidRenderState, HumanoidArm humanoidArm, float f) {
        int side = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        float xRot = humanoidArm == HumanoidArm.RIGHT ? this.rightArm.xRot : this.leftArm.xRot;
        float yRot;

        float g = humanoidRenderState.cookeyMod$itemUseRemainingTicks() - f + 1.0F;
        float h = g / humanoidRenderState.cookeyMod$useItemDuration();
        if (h < -1.0F) return false; // Stop animation from going wild if eating won't process
        float j;
        float k = Math.min(1.0F - (float) Math.pow(h, 27.0D), 1.0F);
        if (h < 0.8F) {
            j = Mth.abs(Mth.cos(g / 4.0F * 3.1415927F) * 0.25F);
            xRot = xRot * 0.5F - 1.57079633F + j;
        } else {
            xRot = k * (xRot * 0.5F - 1.32079633F);
        }

        yRot = side * k * -0.5235988F;

        if (humanoidArm == HumanoidArm.RIGHT) {
            this.rightArm.xRot = xRot;
            this.rightArm.yRot = yRot;
        } else {
            this.leftArm.xRot = xRot;
            this.leftArm.yRot = yRot;
        }

        return true;
    }
}

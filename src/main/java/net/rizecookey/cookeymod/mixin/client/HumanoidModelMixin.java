package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    Option<Boolean> showEatingInThirdPerson = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).showEatingInThirdPerson;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public void addRightEatAnimation(T livingEntity, CallbackInfo ci) {
        HumanoidArm usedHand = livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? livingEntity.getMainArm()
                : livingEntity.getMainArm().getOpposite();
        if (this.showEatingInThirdPerson.get()) {
            if (livingEntity.isUsingItem() && usedHand == HumanoidArm.RIGHT && (livingEntity.getUseItem().getUseAnimation() == UseAnim.EAT || livingEntity.getUseItem().getUseAnimation() == UseAnim.DRINK)) {
                boolean run = this.applyEatingAnimation(livingEntity, usedHand, ((MinecraftAccessor) Minecraft.getInstance()).getTimer().partialTick);
                if (run) ci.cancel();
            }
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public void addLeftEatAnimation(T livingEntity, CallbackInfo ci) {
        HumanoidArm usedHand = livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND
                ? livingEntity.getMainArm()
                : livingEntity.getMainArm().getOpposite();
        if (this.showEatingInThirdPerson.get()) {
            if (livingEntity.isUsingItem() && usedHand == HumanoidArm.LEFT && (livingEntity.getUseItem().getUseAnimation() == UseAnim.EAT || livingEntity.getUseItem().getUseAnimation() == UseAnim.DRINK)) {
                boolean run = this.applyEatingAnimation(livingEntity, usedHand, ((MinecraftAccessor) Minecraft.getInstance()).getTimer().partialTick);
                if (run) ci.cancel();
            }
        }
    }

    // Animation values and "formula" from ItemInHandRenderer's applyEatAnimation
    public boolean applyEatingAnimation(LivingEntity livingEntity, HumanoidArm humanoidArm, float f) {
        int side = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        float xRot = humanoidArm == HumanoidArm.RIGHT ? this.rightArm.xRot : this.leftArm.xRot;
        float yRot;

        float g = livingEntity.getUseItemRemainingTicks() - f + 1.0F;
        float h = g / livingEntity.getUseItem().getUseDuration();
        float j;
        float k = Math.min(1.0F - (float) Math.pow(h, 27.0D), 1.0F);
        if (h < 0.8F) {
            j = Mth.abs(Mth.cos(g / 4.0F * 3.1415927F) * 0.25F);
            xRot = xRot * 0.5F - 1.57079633F + j;
        }
        else {
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

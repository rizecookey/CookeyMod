package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f);

    @Shadow
    protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f);

    @Shadow
    public abstract void renderItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i);

    @Shadow
    private ItemStack offHandItem;

    @Shadow
    private ItemStack mainHandItem;

    AnimationsCategory animationsCategory = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class);
    HudRenderingCategory hudRenderingCategory = CookeyMod.getInstance().getConfig().getCategory(HudRenderingCategory.class);

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    public void onRenderArmWithItem(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        if (hudRenderingCategory.onlyShowShieldWhenBlocking.get() || animationsCategory.enableToolBlocking.get()) {
            if (itemStack.getItem() instanceof ShieldItem && !(!abstractClientPlayer.getUseItem().isEmpty() && abstractClientPlayer.getUseItem().getItem() instanceof ShieldItem)) {
                ci.cancel();
            }
        }
        if (animationsCategory.enableToolBlocking.get()) {
            ItemStack otherHandItem = interactionHand == InteractionHand.MAIN_HAND ? this.offHandItem : this.mainHandItem;
            if (itemStack.getItem() instanceof ShieldItem) {
                if (otherHandItem.getItem() instanceof TieredItem && (!abstractClientPlayer.getUseItem().isEmpty() && abstractClientPlayer.getUseItem().getItem() instanceof ShieldItem)) {
                    ci.cancel();
                }
            }
            if (abstractClientPlayer.getUsedItemHand() != interactionHand && (!abstractClientPlayer.getUseItem().isEmpty() && abstractClientPlayer.getUseItem().getItem() instanceof ShieldItem) && itemStack.getItem() instanceof TieredItem) {
                poseStack.pushPose();
                HumanoidArm humanoidArm = interactionHand == InteractionHand.MAIN_HAND
                        ? abstractClientPlayer.getMainArm()
                        : abstractClientPlayer.getMainArm().getOpposite();
                this.applyItemArmTransform(poseStack, humanoidArm, i);
                this.applyItemBlockTransform(poseStack, humanoidArm);
                if (animationsCategory.swingAndUseItem.get()) {
                    this.applyItemArmAttackTransform(poseStack, humanoidArm, h);
                }
                boolean isRightHand = humanoidArm == HumanoidArm.RIGHT;
                this.renderItem(abstractClientPlayer, itemStack, isRightHand ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isRightHand, poseStack, multiBufferSource, j);

                poseStack.popPose();
                ci.cancel();
            }
        }
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isInvisible()Z", ordinal = 0))
    public boolean makeArmAppear(AbstractClientPlayer instance) {
        return !hudRenderingCategory.showHandWhenInvisible.get() && instance.isInvisible();
    }

    @Redirect(method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmAttackTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V",
                    ordinal = 1))
    public void cancelAttackTransform(ItemInHandRenderer itemInHandRenderer, PoseStack poseStack, HumanoidArm humanoidArm, float f) {
        if (!animationsCategory.swingAndUseItem.get())
            this.applyItemArmAttackTransform(poseStack, humanoidArm, f);
    }

    @Inject(method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
                    ordinal = 1, shift = At.Shift.BEFORE))
    public void injectAttackTransform(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        HumanoidArm humanoidArm = interactionHand == InteractionHand.MAIN_HAND
                ? abstractClientPlayer.getMainArm()
                : abstractClientPlayer.getMainArm().getOpposite();
        if (animationsCategory.swingAndUseItem.get() && !abstractClientPlayer.isAutoSpinAttack()) {
            this.applyItemArmAttackTransform(poseStack, humanoidArm, h);
        }
    }

    @ModifyVariable(method = "tick", slice = @Slice(
            from = @At(value = "JUMP", ordinal = 3)
    ), at = @At(value = "FIELD", ordinal = 0))
    public float modifyArmHeight(float f) {
        double offset = hudRenderingCategory.attackCooldownHandOffset.get();
        return (float) (f * (1 - offset) + offset);
    }

    /* Values from 15w33b, thanks to Fuzss for providing them
    https://github.com/Fuzss/swordblockingcombat/blob/1.15/src/main/java/com/fuzs/swordblockingcombat/client/handler/RenderBlockingHandler.java
     */
    public void applyItemBlockTransform(PoseStack poseStack, HumanoidArm humanoidArm) {
        int reverse = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(reverse * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
        poseStack.mulPose(Axis.YP.rotationDegrees(reverse * 13.365F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(reverse * 78.05F));
    }
}

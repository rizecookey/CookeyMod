package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.FirstPersonDamageRenderSelection;
import net.rizecookey.cookeymod.util.ItemUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm arm, float attackValue);

    @Shadow
    protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float inverseArmHeight);

    @Shadow
    public abstract void renderItem(LivingEntity mob, ItemStack itemStack, ItemDisplayContext type, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords);

    @Shadow
    private ItemStack offHandItem;

    @Shadow
    private ItemStack mainHandItem;


    @Shadow @Final private Minecraft minecraft;
    @Unique
    private AnimationsCategory animationsCategory;

    @Unique
    private HudRenderingCategory hudRenderingCategory;

    @Unique
    private MiscCategory miscCategory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        animationsCategory = modConfig.animations();
        hudRenderingCategory = modConfig.hudRendering();
        miscCategory = modConfig.misc();
    }

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if ((hudRenderingCategory.onlyShowShieldWhenBlocking().get() || animationsCategory.enableToolBlocking().get())
                && (itemStack.getItem() instanceof ShieldItem && !(!player.getUseItem().isEmpty() && player.getUseItem().getItem() instanceof ShieldItem))) {
            ci.cancel();

        }
        if (animationsCategory.enableToolBlocking().get()) {
            ItemStack otherHandItem = hand == InteractionHand.MAIN_HAND ? this.offHandItem : this.mainHandItem;
            if (itemStack.getItem() instanceof ShieldItem && (ItemUtils.isToolItem(otherHandItem.getItem()) && (!player.getUseItem().isEmpty() && player.getUseItem().getItem() instanceof ShieldItem))) {
                ci.cancel();
            }

            if (player.getUsedItemHand() != hand && ((!player.getUseItem().isEmpty() && player.getUseItem().getItem() instanceof ShieldItem)) && ItemUtils.isToolItem(itemStack.getItem())) {
                poseStack.pushPose();
                HumanoidArm humanoidArm = hand == InteractionHand.MAIN_HAND
                        ? player.getMainArm()
                        : player.getMainArm().getOpposite();
                this.applyItemArmTransform(poseStack, humanoidArm, inverseArmHeight);
                this.applyItemBlockTransform(poseStack, humanoidArm);
                if (animationsCategory.swingAndUseItem().get()) {
                    this.applyItemArmAttackTransform(poseStack, humanoidArm, attack);
                }
                boolean isRightHand = humanoidArm == HumanoidArm.RIGHT;
                this.renderItem(player, itemStack, isRightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, poseStack, submitNodeCollector, lightCoords);

                poseStack.popPose();
                ci.cancel();
            }
        }
    }

    @ModifyExpressionValue(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isInvisible()Z"))
    private boolean makeArmAppear(boolean original) {
        return !hudRenderingCategory.showHandWhenInvisible().get() && original;
    }

    @ModifyExpressionValue(method = {"renderOneHandedMap", "renderTwoHandedMap"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isInvisible()Z"))
    private boolean makeArmAppearWithMap(boolean original) {
        return !hudRenderingCategory.showHandWhenInvisible().get() && original;
    }

    @Inject(method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
                    ordinal = 1))
    private void injectAttackTransform(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        HumanoidArm humanoidArm = hand == InteractionHand.MAIN_HAND
                ? player.getMainArm()
                : player.getMainArm().getOpposite();
        if (animationsCategory.swingAndUseItem().get() && player.isUsingItem()) {
            this.applyItemArmAttackTransform(poseStack, humanoidArm, attack);
        }
    }

    @ModifyVariable(method = "tick", slice = @Slice(
            from = @At(value = "JUMP", ordinal = 3)
    ), at = @At(value = "FIELD", ordinal = 0))
    private float modifyArmHeight(float f) {
        if (miscCategory.fixCooldownDesync().get() && minecraft.cookeyMod$isHoldingDownOnBlock()) {
            return 1.0f;
        }
        double offset = hudRenderingCategory.attackCooldownHandOffset().get();
        return (float) (f * (1 - offset) + offset);
    }

    /* Values from 15w33b, thanks to Fuzss for providing them
    https://github.com/Fuzss/swordblockingcombat/blob/1.15/src/main/java/com/fuzs/swordblockingcombat/client/handler/RenderBlockingHandler.java
     */

    @Unique
    private void applyItemBlockTransform(PoseStack poseStack, HumanoidArm humanoidArm) {
        int reverse = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(reverse * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
        poseStack.mulPose(Axis.YP.rotationDegrees(reverse * 13.365F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(reverse * 78.05F));
    }

    @Inject(method = "renderPlayerArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getSkin()Lnet/minecraft/world/entity/player/PlayerSkin;"))
    private void updateInvisibilityAndOverlayCoordsOnPlayerArmRender(CallbackInfo ci, @Local(name = "avatarRenderer") AvatarRenderer<AbstractClientPlayer> avatarRenderer) {
        updateInvisibilityAndOverlayCoords(avatarRenderer);
    }

    @Inject(method = "renderMapHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getSkin()Lnet/minecraft/world/entity/player/PlayerSkin;"))
    private void updateInvisibilityAndOverlayCoordsOnMapHandRender(CallbackInfo ci, @Local(name = "avatarRenderer") AvatarRenderer<AbstractClientPlayer> avatarRenderer) {
        updateInvisibilityAndOverlayCoords(avatarRenderer);
    }

    @Unique
    private void updateInvisibilityAndOverlayCoords(AvatarRenderer<AbstractClientPlayer> avatarRenderer) {
        LocalPlayer player = this.minecraft.player;
        assert player != null;
        boolean damageTintVisible = player.hurtTime > 0 || player.deathTime > 0;
        int coords = OverlayTexture.pack(0.0f, damageTintVisible);
        avatarRenderer.cookeyMod$setOverlayCoords(coords);
        avatarRenderer.cookeyMod$setPlayerInvisible(this.minecraft.player.isInvisible());
    }

    @ModifyArg(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), index = 3)
    private int calculateOverlayCoords(int original) {
        if (hudRenderingCategory.showDamageTintInFirstPerson().get() != FirstPersonDamageRenderSelection.HANDS_AND_ITEMS) {
            return original;
        }

        LocalPlayer player = this.minecraft.player;
        assert player != null;
        return OverlayTexture.pack(0.0f, player.hurtTime > 0 || player.deathTime > 0);
    }
}

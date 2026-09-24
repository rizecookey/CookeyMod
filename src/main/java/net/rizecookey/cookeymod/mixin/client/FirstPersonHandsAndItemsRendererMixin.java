package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.setting.FirstPersonDamageRenderSelection;
import net.rizecookey.cookeymod.util.ItemUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonHandsAndItemsRendererMixin {
    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm arm, float attackValue);

    @Shadow
    protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float inverseArmHeight);


    @Shadow @Final private Minecraft minecraft;
    @Unique
    private AnimationsCategory animationsCategory;

    @Unique
    private HudRenderingCategory hudRenderingCategory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        animationsCategory = modConfig.animations();
        hudRenderingCategory = modConfig.hudRendering();
    }

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        AvatarRenderState avatarRenderState = playerState.avatarRenderState;
        if (avatarRenderState == null) return;

        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
        ItemStack useItem = avatarRenderState.useItemHand == InteractionHand.MAIN_HAND ? state.mainHandItem : state.offHandItem;
        if ((hudRenderingCategory.onlyShowShieldWhenBlocking().get() || animationsCategory.enableToolBlocking().get())
                && itemStack.getItem() instanceof ShieldItem && (!avatarRenderState.isUsingItem || avatarRenderState.useItemHand != hand)) {
            ci.cancel();

        }
        if (!animationsCategory.enableToolBlocking().get() || !avatarRenderState.isUsingItem) {
            return;
        }

        ItemStack otherHandItem = hand == InteractionHand.MAIN_HAND ? state.offHandItem : state.mainHandItem;
        if (avatarRenderState.useItemHand == hand && itemStack.getItem() instanceof ShieldItem && ItemUtils.isToolItem(otherHandItem.getItem())) {
            ci.cancel();
        }

        if (avatarRenderState.useItemHand != hand && useItem.getItem() instanceof ShieldItem && ItemUtils.isToolItem(itemStack.getItem())) {
            poseStack.pushPose();
            HumanoidArm humanoidArm = hand == InteractionHand.MAIN_HAND
                    ? avatarRenderState.mainArm
                    : avatarRenderState.mainArm.getOpposite();
            this.applyItemArmTransform(poseStack, humanoidArm, inverseArmHeight);
            this.applyItemBlockTransform(poseStack, humanoidArm);
            if (animationsCategory.swingAndUseItem().get()) {
                this.applyItemArmAttackTransform(poseStack, humanoidArm, attack);
            }
            (isMainHand ? state.mainHandRenderState : state.offHandRenderState).submit(
                    poseStack,
                    submitNodeCollector,
                    lightCoords,
                    calculateOverlayCoords(OverlayTexture.NO_OVERLAY),
                    0);

            poseStack.popPose();
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "submitArmWithItem", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;isInvisible:Z", opcode = Opcodes.GETFIELD))
    private boolean makeArmAppear(boolean original) {
        return !hudRenderingCategory.showHandWhenInvisible().get() && original;
    }

    @ModifyExpressionValue(method = {"renderOneHandedMap", "renderTwoHandedMap"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;isInvisible:Z", opcode = Opcodes.GETFIELD))
    private boolean makeArmAppearWithMap(boolean original) {
        return !hudRenderingCategory.showHandWhenInvisible().get() && original;
    }

    @Inject(method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V",
                    ordinal = 1))
    private void injectAttackTransform(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        AvatarRenderState avatarRenderState = playerState.avatarRenderState;
        if (avatarRenderState == null) return;

        HumanoidArm humanoidArm = hand == InteractionHand.MAIN_HAND
                ? avatarRenderState.mainArm
                : avatarRenderState.mainArm.getOpposite();
        if (animationsCategory.swingAndUseItem().get() && avatarRenderState.isUsingItem) {
            this.applyItemArmAttackTransform(poseStack, humanoidArm, attack);
        }
    }

    /* Values from 15w33b, thanks to Fuzss for providing them
    https://github.com/Fuzss/swordblockingcombat/blob/1.15/src/main/java/com/fuzs/swordblockingcombat/client/handler/RenderBlockingHandler.java
     */

    @Unique
    private void applyItemBlockTransform(PoseStack poseStack, HumanoidArm humanoidArm) {
        int reverse = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(reverse * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.rotateDegrees(Axis.XP, -102.25F);
        poseStack.rotateDegrees(Axis.YP, reverse * 13.365F);
        poseStack.rotateDegrees(Axis.ZP, reverse * 78.05F);
    }

    @Inject(method = "renderPlayerHand", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;skin:Lnet/minecraft/world/entity/player/PlayerSkin;"))
    private void updateInvisibilityAndOverlayCoordsOnPlayerArmRender(CallbackInfo ci, @Local(name = "avatarRenderer") AvatarRenderer<AbstractClientPlayer> avatarRenderer) {
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

    @Unique
    private int calculateOverlayCoords(int original) {
        if (hudRenderingCategory.showDamageTintInFirstPerson().get() != FirstPersonDamageRenderSelection.HANDS_AND_ITEMS) {
            return original;
        }

        LocalPlayer player = this.minecraft.player;
        assert player != null;
        return OverlayTexture.pack(0.0f, player.hurtTime > 0 || player.deathTime > 0);
    }

    @ModifyArg(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), index = 3)
    private int passOverlayCoordsOnSubmit(int original) {
        return calculateOverlayCoords(original);
    }
}

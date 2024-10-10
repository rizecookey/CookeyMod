package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.extension.minecraft.PlayerRendererExtension;
import net.rizecookey.cookeymod.util.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> implements PlayerRendererExtension {
    @Unique
    private static BooleanOption ENABLE_TOOL_BLOCKING;

    @Unique
    private BooleanOption shownHandWhenInvisible;

    @Unique
    private DoubleSliderOption invisibilityHandOpacity;

    @Unique
    private boolean playerInvisible;

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(EntityRendererProvider.Context context, boolean bl, CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        shownHandWhenInvisible = modConfig.hudRendering().showHandWhenInvisible();
        invisibilityHandOpacity = modConfig.hudRendering().invisibilityHandOpacity();
        ENABLE_TOOL_BLOCKING = modConfig.animations().enableToolBlocking();
    }

    @Inject(method = "getArmPose(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState$HandState;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void addItemBlockPose(PlayerRenderState playerRenderState, PlayerRenderState.HandState handState, InteractionHand interactionHand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!ENABLE_TOOL_BLOCKING.get()) {
            return;
        }

        ItemStack currentHandStack = playerRenderState.cookeyMod$getItemInHand(interactionHand);
        ItemStack otherHandStack = playerRenderState.cookeyMod$getItemInHand(interactionHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (playerRenderState.isUsingItem && playerRenderState.cookeyMod$getUsedItem().getItem() instanceof ShieldItem) {
            if (ItemUtils.isToolItem(currentHandStack.getItem()) && otherHandStack.getItem() instanceof ShieldItem) {
                cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
            } else if (currentHandStack.getItem() instanceof ShieldItem && ItemUtils.isToolItem(otherHandStack.getItem())) {
                cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
            }
        }
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    public void transparentHandWhenInvisible(ModelPart instance, PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, @Local(argsOnly = true) MultiBufferSource multiBufferSource, @Local(argsOnly = true) ResourceLocation resourceLocation) {
        if (shownHandWhenInvisible.get() && playerInvisible) {
            var color = ARGB.color((int) (invisibilityHandOpacity.get() * 0xFFL), 0xFF, 0xFF, 0xFF);
            instance.render(poseStack, multiBufferSource.getBuffer(RenderType.itemEntityTranslucentCull(resourceLocation)), i, j, color);
        } else {
            instance.render(poseStack, vertexConsumer, i, j);
        }
    }

    @Override
    public void cookeyMod$setPlayerInvisible(boolean invisible) {
        this.playerInvisible = invisible;
    }
}

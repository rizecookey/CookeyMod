package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import net.rizecookey.cookeymod.config.option.EnumOption;
import net.rizecookey.cookeymod.config.option.FirstPersonDamageRenderSelection;
import net.rizecookey.cookeymod.extension.minecraft.AvatarRendererExtension;
import net.rizecookey.cookeymod.util.ItemUtils;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<@NonNull AvatarlikeEntity, AvatarRenderState, PlayerModel> implements AvatarRendererExtension {
    @Unique
    private static BooleanOption ENABLE_TOOL_BLOCKING;

    @Unique
    private BooleanOption shownHandWhenInvisible;
    @Unique
    private EnumOption<FirstPersonDamageRenderSelection> showDamageTintInFirstPerson;

    @Unique
    private DoubleSliderOption invisibilityHandOpacity;

    @Unique
    private boolean playerInvisible;

    @Unique
    private int overlayCoords;

    public AvatarRendererMixin(EntityRendererProvider.Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        shownHandWhenInvisible = modConfig.hudRendering().showHandWhenInvisible();
        showDamageTintInFirstPerson = modConfig.hudRendering().showDamageTintInFirstPerson();
        invisibilityHandOpacity = modConfig.hudRendering().invisibilityHandOpacity();
        ENABLE_TOOL_BLOCKING = modConfig.animations().enableToolBlocking();
    }

    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", at = @At("HEAD"), cancellable = true)
    private static void addItemBlockPose(Avatar avatar, ItemStack itemInHand, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!ENABLE_TOOL_BLOCKING.get()) {
            return;
        }

        ItemStack currentHandStack = avatar.getItemInHand(hand);
        ItemStack otherHandStack = avatar.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (avatar.isUsingItem() && avatar.getUseItem().getItem() instanceof ShieldItem) {
            if (ItemUtils.isToolItem(currentHandStack.getItem()) && otherHandStack.getItem() instanceof ShieldItem) {
                cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
            } else if (currentHandStack.getItem() instanceof ShieldItem && ItemUtils.isToolItem(otherHandStack.getItem())) {
                cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
            }
        }
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModelPart(Lnet/minecraft/client/model/geom/ModelPart;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    public void transparentHandWhenInvisible(SubmitNodeCollector instance, ModelPart modelPart, PoseStack poseStack, RenderType renderType, int i, int j, TextureAtlasSprite textureAtlasSprite, @Local(argsOnly = true, name = "skinTexture") Identifier skinTexture) {
        int overlayCoords = showDamageTintInFirstPerson.get().isOnHand() ? this.overlayCoords : j;
        if (shownHandWhenInvisible.get() && playerInvisible) {
            int color = ARGB.color((int) (invisibilityHandOpacity.get() * 0xFFL), 0xFF, 0xFF, 0xFF);
            instance.submitModelPart(modelPart, poseStack, RenderTypes.entityTranslucentCullItemTarget(skinTexture), i, overlayCoords, textureAtlasSprite, color, null);
        } else {
            instance.submitModelPart(modelPart, poseStack, renderType, i, overlayCoords, textureAtlasSprite);
        }
    }

    @Override
    public void cookeyMod$setPlayerInvisible(boolean invisible) {
        this.playerInvisible = invisible;
    }

    @Override
    public void cookeyMod$setOverlayCoords(int overlayCoords) {
        this.overlayCoords = overlayCoords;
    }
}

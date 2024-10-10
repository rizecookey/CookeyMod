package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.util.ItemUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin<S extends LivingEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
    private ItemInHandLayerMixin(RenderLayerParent<S, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Unique
    private BooleanOption enableToolBlocking;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(RenderLayerParent<S, M> renderLayerParent, ItemRenderer itemRenderer, CallbackInfo ci) {
        enableToolBlocking = CookeyMod.getInstance().getConfig().animations().enableToolBlocking();
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    public void hideShieldWithToolBlocking(S livingEntityRenderState, @Nullable BakedModel bakedModel, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (!enableToolBlocking.get() || !(livingEntityRenderState instanceof HumanoidRenderState humanoidRenderState)) {
            return;
        }

        HumanoidArm otherArm = humanoidArm.getOpposite();
        ItemStack otherHandStack = otherArm == HumanoidArm.RIGHT ? livingEntityRenderState.rightHandItem : livingEntityRenderState.leftHandItem;
        if (itemStack.getItem() instanceof ShieldItem && ItemUtils.isToolItem(otherHandStack.getItem()) && (humanoidRenderState.isUsingItem && humanoidRenderState.cookeyMod$getUsedItem().getItem() instanceof ShieldItem)) {
            ci.cancel();
        }
    }
}

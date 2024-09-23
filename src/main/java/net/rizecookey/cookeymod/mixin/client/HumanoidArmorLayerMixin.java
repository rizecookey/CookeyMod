package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.LivingEntity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.annotation.mixin.Incompatible;
import net.rizecookey.cookeymod.config.option.ArmorDamageRenderSelection;
import net.rizecookey.cookeymod.config.option.EnumOption;
import net.rizecookey.cookeymod.extension.minecraft.OverlayRendered;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
@Incompatible("optifabric")
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> implements OverlayRendered<T> {
    @Shadow
    public abstract void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l);

    @Unique
    private int overlayCoords;

    @Unique
    private EnumOption<ArmorDamageRenderSelection> showDamageTintOnArmor;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(RenderLayerParent<T, M> renderLayerParent, A humanoidModel, A humanoidModel2, ModelManager modelManager, CallbackInfo ci) {
        showDamageTintOnArmor = CookeyMod.getInstance().getConfig().hudRendering().showDamageTintOnArmor();
    }

    @ModifyArg(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), index = 3)
    public int modifyOverlayCoords(int previousCoords) {
        boolean show = showDamageTintOnArmor.get().isOnRegularArmor();
        return show ? this.overlayCoords : previousCoords;
    }

    @ModifyArg(method = "renderTrim", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"), index = 3)
    public int modifyTrimOverlayCoords(int previousCoords) {
        boolean show = showDamageTintOnArmor.get() == ArmorDamageRenderSelection.ARMOR_AND_TRIM;
        return show ? this.overlayCoords : previousCoords;
    }

    @Override
    public void cookeyMod$renderWithOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l, int overlayCoords) {
        this.overlayCoords = overlayCoords;
        this.render(poseStack, multiBufferSource, i, entity, f, g, h, j, k, l);
    }
}

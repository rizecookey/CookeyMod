package net.rizecookey.cookeymod.mixin.client.compatibility.optifabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.annotation.mixin.ModSpecific;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.option.Option;
import net.rizecookey.cookeymod.extension.OverlayRendered;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HumanoidArmorLayer.class)
@ModSpecific("optifabric")
public abstract class OFHumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> implements OverlayRendered<T> {
    @Shadow
    public abstract void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l);

    int overlayCoords;
    Option<Boolean> showDamageTintOnArmor = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).showDamageTintOnArmor;

    @Redirect(method = "*", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I"))
    public int modifyOverlayCoords() {
        boolean show = this.showDamageTintOnArmor.get();
        return show ? this.overlayCoords : OverlayTexture.NO_OVERLAY;
    }

    @Override
    public void renderWithOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l, int overlayCoords) {
        this.overlayCoords = overlayCoords;
        this.render(poseStack, multiBufferSource, i, entity, f, g, h, j, k, l);
    }
}

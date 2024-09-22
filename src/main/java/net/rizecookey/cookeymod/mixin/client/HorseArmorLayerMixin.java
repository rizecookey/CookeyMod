package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.world.entity.animal.horse.Horse;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.ArmorDamageRenderSelection;
import net.rizecookey.cookeymod.config.option.EnumOption;
import net.rizecookey.cookeymod.extension.OverlayRendered;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HorseArmorLayer.class)
public abstract class HorseArmorLayerMixin implements OverlayRendered<Horse> {
    @Shadow
    public abstract void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Horse horse, float f, float g, float h, float j, float k, float l);


    @Unique
    private int overlayCoords;

    @Unique
    private EnumOption<ArmorDamageRenderSelection> showDamageTintOnArmor;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(RenderLayerParent<Horse, HorseModel<Horse>> renderLayerParent, EntityModelSet entityModelSet, CallbackInfo ci) {
        showDamageTintOnArmor = CookeyMod.getInstance().getConfig().hudRendering().showDamageTintOnArmor();
    }

    @ModifyArg(method = "render*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HorseModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), index = 3)
    public int modifyOverlayCoords(int previousCoords) {
        boolean show = showDamageTintOnArmor.get().isOnRegularArmor();
        return show ? this.overlayCoords : previousCoords;
    }

    @Override
    public void cookeyMod$renderWithOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Horse entity, float f, float g, float h, float j, float k, float l, int overlayCoords) {
        this.overlayCoords = overlayCoords;
        this.render(poseStack, multiBufferSource, i, entity, f, g, h, j, k, l);
    }
}

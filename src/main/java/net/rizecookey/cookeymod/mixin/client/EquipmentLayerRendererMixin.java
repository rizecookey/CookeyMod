package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.EquipmentModelSet;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.ArmorDamageRenderSelection;
import net.rizecookey.cookeymod.config.option.EnumOption;
import net.rizecookey.cookeymod.extension.minecraft.OverlayRendered;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin implements OverlayRendered {
    @Unique
    private int overlayCoords;

    @Unique
    private EnumOption<ArmorDamageRenderSelection> showDamageTintOnArmor;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(EquipmentModelSet equipmentModelSet, TextureAtlas textureAtlas, CallbackInfo ci) {
        showDamageTintOnArmor = CookeyMod.getInstance().getConfig().hudRendering().showDamageTintOnArmor();
    }

    @ModifyExpressionValue(method = "renderLayers(Lnet/minecraft/world/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getArmorFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Z)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
            ),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I", opcode = Opcodes.GETSTATIC, ordinal = 0))
    public int modifyOverlayCoords(int previousCoords) {
        boolean show = showDamageTintOnArmor.get().isOnRegularArmor();
        return show ? this.overlayCoords : previousCoords;
    }

    @ModifyExpressionValue(method = "renderLayers(Lnet/minecraft/world/item/equipment/EquipmentModel$LayerType;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;trimSpriteLookup:Ljava/util/function/Function;", opcode = Opcodes.GETFIELD)
            ),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/texture/OverlayTexture;NO_OVERLAY:I", opcode = Opcodes.GETSTATIC, ordinal = 0))
    public int modifyTrimOverlayCoords(int previousCoords) {
        boolean show = showDamageTintOnArmor.get() == ArmorDamageRenderSelection.ARMOR_AND_TRIM;
        return show ? this.overlayCoords : previousCoords;
    }

    @Override
    public void cookeyMod$setOverlayCoords(int overlayCoords) {
        this.overlayCoords = overlayCoords;
    }
}

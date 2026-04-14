package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.ArmorDamageRenderSelection;
import net.rizecookey.cookeymod.config.option.EnumOption;
import net.rizecookey.cookeymod.extension.minecraft.OverlayRendered;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin implements OverlayRendered {
    @Unique private int overlayCoords;

    @Override
    public void cookeyMod$setOverlayCoords(int overlayCoords) {
        this.overlayCoords = overlayCoords;
    }

    @Unique
    private EnumOption<ArmorDamageRenderSelection> showDamageTintOnArmor;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        showDamageTintOnArmor = CookeyMod.getInstance().getConfig().hudRendering().showDamageTintOnArmor();
    }

    @ModifyArg(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), index = 3)
    private int renderWithOverlay(int original) {
        if (showDamageTintOnArmor.get() == ArmorDamageRenderSelection.NONE) {
            return original;
        }

        return overlayCoords;
    }
}

package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.rizecookey.cookeymod.extension.minecraft.OverlayRendered;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseArmorLayer.class)
public class HorseArmorLayerMixin implements OverlayRendered {
    @Shadow
    @Final
    private EquipmentLayerRenderer equipmentRenderer;

    @Override
    public void cookeyMod$setOverlayCoords(int overlayCoords) {
        this.equipmentRenderer.cookeyMod$setOverlayCoords(overlayCoords);
    }
}

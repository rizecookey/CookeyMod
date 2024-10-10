package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.rizecookey.cookeymod.extension.minecraft.OverlayRendered;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin implements OverlayRendered {
    @Shadow
    @Final
    private EquipmentLayerRenderer equipmentRenderer;

    @Override
    public void cookeyMod$setOverlayCoords(int overlayCoords) {
        this.equipmentRenderer.cookeyMod$setOverlayCoords(overlayCoords);
    }
}

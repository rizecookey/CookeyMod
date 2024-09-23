package net.rizecookey.cookeymod.extension.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface OverlayRendered<T> {
    void cookeyMod$renderWithOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l, int overlayCoords);
}

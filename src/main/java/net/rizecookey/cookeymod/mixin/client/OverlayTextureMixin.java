package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Color;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.event.OverlayReloadListener;
import net.rizecookey.cookeymod.extension.minecraft.OverlayTextureExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin implements OverlayTextureExtension, OverlayReloadListener {
    @Shadow
    @Final
    private DynamicTexture texture;

    @Unique
    private HudRenderingCategory hudRenderingCategory;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modifyHitColor(CallbackInfo ci) {
        hudRenderingCategory = CookeyMod.getInstance().getConfig().hudRendering();
        this.cookeyMod$reloadOverlay();
        OverlayReloadListener.register(this);
    }

    public void cookeyMod$onOverlayReload() {
        this.cookeyMod$reloadOverlay();
    }

    @Unique
    private static int getColorInt(int red, int green, int blue, int alpha) {
        alpha = 255 - alpha;
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

    public void cookeyMod$reloadOverlay() {
        NativeImage nativeImage = this.texture.getPixels();

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    Color color = hudRenderingCategory.damageColor().get();
                    assert nativeImage != null;
                    nativeImage.setPixel(j, i, getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                }
            }
        }

        RenderSystem.activeTexture(33985);
        this.texture.bind();
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }
}
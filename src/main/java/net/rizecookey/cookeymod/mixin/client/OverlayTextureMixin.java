package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Color;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.event.OverlayReloadListener;
import net.rizecookey.cookeymod.extension.OverlayTextureExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin implements OverlayTextureExtension, OverlayReloadListener {
    @Shadow @Final private DynamicTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modifyHitColor(CallbackInfo ci) {
        this.reloadOverlay();
        OverlayReloadListener.register(this);
    }

    public void onOverlayReload() {
        this.reloadOverlay();
    }

    @SuppressWarnings("SameParameterValue")
    private static int getColorInt(int red, int green, int blue, int alpha) {
        alpha = 255 - alpha;
        return (alpha << 24) + (blue << 16) + (green << 8) + red;
    }

    public void reloadOverlay() {
        NativeImage nativeImage = this.texture.getPixels();

        for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
                if (i < 8) {
                    AnimationsCategory animOpt = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class);
                    Color color = animOpt.damageColor.get();
                    nativeImage.setPixelRGBA(j, i, getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                }
            }
        }

        RenderSystem.activeTexture(33985);
        this.texture.bind();
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.06666667F;
        RenderSystem.scalef(f, f, f);
        RenderSystem.matrixMode(5888);
        this.texture.bind();
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }
}
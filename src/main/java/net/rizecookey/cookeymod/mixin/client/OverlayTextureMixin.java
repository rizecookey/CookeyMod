package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.rizecookey.cookeymod.CookeyMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayTexture.class)
public abstract class OverlayTextureMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/renderpearl/api/device/GpuDevice;createTextureView(Lcom/mojang/renderpearl/api/textures/GpuTexture;)Lcom/mojang/renderpearl/api/textures/GpuTextureView;", shift = At.Shift.AFTER))
    private void initialize(CallbackInfo ci, @Share("colorInt") LocalIntRef colorInt) {
        var hudRenderingCategory = CookeyMod.getInstance().getConfig().hudRendering();
        var color = hudRenderingCategory.damageColor().get();
        colorInt.set(getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
    }

    @ModifyArg(method = "<init>",
            slice = @Slice(
                    from = @At(value = "NEW", target = "Lcom/mojang/blaze3d/platform/NativeImage;"),
                    to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;writeToGpuTexture(Lcom/mojang/renderpearl/api/commands/CommandEncoder;Lcom/mojang/renderpearl/api/textures/GpuTexture;)V")
            ),
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;setPixel(III)V"), index = 2)
    private int modifyColor(int pixel, @Local(name = "y") int y, @Share("colorInt") LocalIntRef colorInt) {
        if (y >= 8) {
            return pixel;
        }

        return colorInt.get();
    }

    @Unique
    private static int getColorInt(int red, int green, int blue, int alpha) {
        alpha = 255 - alpha;
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }
}
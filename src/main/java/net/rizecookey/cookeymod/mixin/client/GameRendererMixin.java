package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.AnimationOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow protected abstract void bobView(PoseStack poseStack, float f);

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    public void cancelCameraShake(GameRenderer gameRenderer, PoseStack poseStack, float f) {
        if (!CookeyMod.getInstance().getConfig().getCategory(AnimationOptions.class).isDisableCameraBobbing()) {
            this.bobView(poseStack, f);
        }
    }
}

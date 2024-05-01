package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @Unique
    private BooleanOption disableCameraBobbing = CookeyMod.getInstance().getConfig().animations().disableCameraBobbing();

    @Unique
    private BooleanOption alternativeBobbing = CookeyMod.getInstance().getConfig().hudRendering().alternativeBobbing();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(Minecraft minecraft, ItemInHandRenderer itemInHandRenderer, ResourceManager resourceManager, RenderBuffers renderBuffers, CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        disableCameraBobbing = modConfig.animations().disableCameraBobbing();
        alternativeBobbing = modConfig.hudRendering().alternativeBobbing();
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;screenEffectScale()Lnet/minecraft/client/OptionInstance;")
            )
    )
    private Object modifyBobViewInRenderLevel(OptionInstance<Boolean> instance) {
        return instance.get() && !disableCameraBobbing.get();
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void changeToAlternativeBob(PoseStack poseStack, float f, CallbackInfo ci) {
        if (alternativeBobbing.get()) {
            this.alternativeBobView(poseStack, f);
            ci.cancel();
        }
    }


    @Unique
    private void alternativeBobView(PoseStack poseStack, float f) {
        if (this.minecraft.getCameraEntity() instanceof Player player) {
            float g = player.walkDist - player.walkDistO;
            float h = -(player.walkDist + g * f);
            float i = Mth.lerp(f, player.oBob, player.bob);
            poseStack.translate(Mth.sin(h * 3.1415927F) * i * 0.5F, -Math.abs(Mth.cos(h * 3.1415927F) * i), 0.0D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.cos(h * 3.1415927F) * i * 3.0F));
        }
    }
}

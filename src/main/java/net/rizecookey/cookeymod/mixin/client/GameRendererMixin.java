package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.option.Option;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow protected abstract void bobView(PoseStack poseStack, float f);

    @Shadow @Final private Minecraft minecraft;
    Option<Boolean> disableCameraBobbing = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).disableCameraBobbing;
    Option<Boolean> enableDamageCameraTilt = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).enableDamageCameraTilt;
    Option<Boolean> alternativeBobbing = CookeyMod.getInstance().getConfig().getCategory(HudRenderingCategory.class).alternativeBobbing;

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    public void cancelCameraShake(GameRenderer gameRenderer, PoseStack poseStack, float f) {
        if (!this.disableCameraBobbing.get()) {
            this.bobView(poseStack, f);
        }
    }

    @Redirect(method = "bobHurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", opcode = Opcodes.GETFIELD))
    public int modifyBobHurt(LivingEntity livingEntity) {
        if (this.enableDamageCameraTilt.get() && livingEntity instanceof LocalPlayer) {
            int hurtTime = livingEntity.hurtTime + 1;
            return hurtTime > livingEntity.hurtDuration ? 0 : hurtTime;
        }
        return livingEntity.hurtTime;
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    public void changeToAlternativeBob(PoseStack poseStack, float f, CallbackInfo ci) {
        if (alternativeBobbing.get()) {
            this.alternativeBobView(poseStack, f);
            ci.cancel();
        }
    }

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

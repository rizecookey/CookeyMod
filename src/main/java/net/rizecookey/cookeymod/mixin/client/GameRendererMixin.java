package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.option.Option;
import net.rizecookey.cookeymod.extension.LivingEntityExtension;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow protected abstract void bobView(PoseStack poseStack, float f);

    Option<Boolean> disableCameraBobbing = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).disableCameraBobbing;
    Option<Boolean> enableDamageCameraTilt = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).enableDamageCameraTilt;

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
    public void cancelCameraShake(GameRenderer gameRenderer, PoseStack poseStack, float f) {
        if (!this.disableCameraBobbing.get()) {
            this.bobView(poseStack, f);
        }
    }

    @Redirect(method = "bobHurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", opcode = Opcodes.GETFIELD))
    public int rewriteBobHurt(LivingEntity livingEntity) {
        if (this.enableDamageCameraTilt.get()) {
            LivingEntityExtension ext = ((LivingEntityExtension) livingEntity);
            return ext.hasReceivedKb() ? ext.getHurtTimeKb() : 0;
        }
        return livingEntity.hurtTime;
    }
}

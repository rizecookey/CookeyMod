package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.DoubleSliderOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private Entity entity;

    @Shadow
    private float eyeHeight;

    @Shadow
    private float eyeHeightOld;

    @Shadow
    public abstract Entity entity();

    @Unique
    private DoubleSliderOption sneakAnimationSpeed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        sneakAnimationSpeed = CookeyMod.getInstance().getConfig().animations().sneakAnimationSpeed();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void disableSneakAnimation(CallbackInfo ci) {
        if (this.sneakAnimationSpeed.get() != 0.0 || this.entity == null) {
            return;
        }

        this.eyeHeight = entity().getEyeHeight();
        this.eyeHeightOld = this.eyeHeight;
        ci.cancel();
    }

    @Definition(id = "entity", field = "entity")
    @Definition(id = "getEyeHeight", method = "Lnet/minecraft/world/entity/Entity;getEyeHeight()F")
    @Definition(id = "eyeHeight", field = "eyeHeight")
    @Expression("(this.entity.getEyeHeight() - this.eyeHeight)")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float setSneakAnimationSpeed(float original) {
        return (float) (original * sneakAnimationSpeed.get());
    }
}

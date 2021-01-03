package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.extension.LivingEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityExtension {
    boolean receivedKb = false;
    int hurtTimeKb;

    // Default constructor to satisfy compiler :P
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getAttackAnim", at = @At(value = "RETURN", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void changeAttackAnim(float f, CallbackInfoReturnable<Float> cir, float g, float h) {
        if (CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).enableOldSwing.get()) {
            cir.setReturnValue(h);
        }
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void lowerHurtTime(CallbackInfo ci) {
        if (isDamageTilt() && this.hurtTimeKb > 0 && this.receivedKb) {
            --this.hurtTimeKb;
        }
    }

    @Inject(method = "hurt", at = @At("TAIL"))
    public void setReceivedKb(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        if (this.isDamageTilt()) {
            this.setReceivedKb(false);
        }
    }

    public boolean hasReceivedKb() {
        return receivedKb;
    }

    public void setReceivedKb(boolean receivedKb) {
        this.receivedKb = receivedKb;
    }

    public int getHurtTimeKb() {
        return hurtTimeKb;
    }

    public void setHurtTimeKb(int hurtTimeKb) {
        this.hurtTimeKb = hurtTimeKb;
    }

    public boolean isDamageTilt() {
        return CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).enableDamageCameraTilt.get();
    }
}

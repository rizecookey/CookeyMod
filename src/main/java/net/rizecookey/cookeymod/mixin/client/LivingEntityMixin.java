package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    AnimationsCategory animationsCategory = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class);

    // Default constructor to satisfy compiler :P
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getAttackAnim", at = @At(value = "RETURN", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void changeAttackAnim(float f, CallbackInfoReturnable<Float> cir, float g, float h) {
        if (animationsCategory.enableOldSwing.get()) {
            cir.setReturnValue(h);
        }
    }
}

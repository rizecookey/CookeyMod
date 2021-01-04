package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    Option<Boolean> force100PercentRecharge = CookeyMod.getInstance().getConfig().getCategory(MiscCategory.class).force100PercentRecharge;

    @Inject(method = "isAttackAvailable", at = @At(value = "RETURN"), cancellable = true)
    public void disable4TickSwinging(float f, CallbackInfoReturnable<Boolean> cir) {
        if (this.getAttackStrengthScale(f) < 1.0F && this.force100PercentRecharge.get()) {
            cir.setReturnValue(false);
        }
    }
}

package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.extension.PlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerExtension {
    @Shadow
    public abstract void resetAttackStrengthTicker();

    BooleanOption fixCooldownDesync = CookeyMod.getInstance().getConfig().misc().fixCooldownDesync();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setAttackStrengthTicker(int ticks) {
        this.attackStrengthTicker = ticks;
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    public void resetAttackStrengthOnDrop(CallbackInfoReturnable<ItemEntity> cir) {
        if (fixCooldownDesync.get() && cir.getReturnValue() != null && this.level().isClientSide())
            this.resetAttackStrengthTicker();
    }
}

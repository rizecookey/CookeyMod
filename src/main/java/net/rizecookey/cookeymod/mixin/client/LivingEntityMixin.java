package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    AnimationsCategory animationsCategory = CookeyMod.getInstance().getConfig().animations();

    // Default constructor to satisfy compiler :P
    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}

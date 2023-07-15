package net.rizecookey.cookeymod.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    BooleanOption disableEffectBasedFovChange = CookeyMod.getInstance().getConfig().getCategory(HudRenderingCategory.class).disableEffectBasedFovChange();

    private AbstractClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"))
    public double disableEffectBasedFov(AbstractClientPlayer player, Attribute attribute) {
        if (this.disableEffectBasedFovChange.get()) {
            double mov = player.getAttributeBaseValue(attribute);
            if (this.isSprinting() && LivingEntityAccessor.SPEED_MODIFIER_SPRINTING() != null) {
                mov *= 1 + LivingEntityAccessor.SPEED_MODIFIER_SPRINTING().getAmount();
            }
            return mov;
        } else {
            return player.getAttributeValue(attribute);
        }
    }
}

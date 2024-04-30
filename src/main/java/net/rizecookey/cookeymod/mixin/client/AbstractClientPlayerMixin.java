package net.rizecookey.cookeymod.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    @Unique
    private BooleanOption disableEffectBasedFovChange;

    private AbstractClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(ClientLevel clientLevel, GameProfile gameProfile, CallbackInfo ci) {
        disableEffectBasedFovChange = CookeyMod.getInstance().getConfig().hudRendering().disableEffectBasedFovChange();
    }

    @Redirect(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    public double disableEffectBasedFov(AbstractClientPlayer instance, Holder<Attribute> attribute) {
        if (disableEffectBasedFovChange.get()) {
            double mov = instance.getAttributeBaseValue(attribute);
            if (this.isSprinting() && LivingEntityAccessor.SPEED_MODIFIER_SPRINTING() != null) {
                mov *= 1 + LivingEntityAccessor.SPEED_MODIFIER_SPRINTING().amount();
            }
            return mov;
        } else {
            return instance.getAttributeValue(attribute);
        }
    }
}

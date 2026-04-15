package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    @Unique
    private BooleanOption disableEffectBasedFovChange;

    private AbstractClientPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        disableEffectBasedFovChange = CookeyMod.getInstance().getConfig().hudRendering().disableEffectBasedFovChange();
    }

    @ModifyExpressionValue(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double disableEffectBasedFov(double original) {
        if (!disableEffectBasedFovChange.get()) {
            return original;
        }
        double mov = getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        if (this.isSprinting() && LivingEntityAccessor.SPEED_MODIFIER_SPRINTING() != null) {
            mov *= 1 + LivingEntityAccessor.SPEED_MODIFIER_SPRINTING().amount();
        }
        return mov;
    }
}

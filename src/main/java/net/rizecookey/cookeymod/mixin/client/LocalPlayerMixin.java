package net.rizecookey.cookeymod.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Unique
    private BooleanOption fixCooldownDesync;

    private LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        fixCooldownDesync = CookeyMod.getInstance().getConfig().misc().fixCooldownDesync();
    }

    @Inject(method = "swing", at = @At("TAIL"))
    public void resetAttackStrengthOnSwing(InteractionHand hand, CallbackInfo ci) {
        if (fixCooldownDesync.get()) {
            this.resetAttackStrengthTicker();
        }
    }
}

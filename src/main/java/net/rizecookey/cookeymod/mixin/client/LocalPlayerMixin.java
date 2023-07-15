package net.rizecookey.cookeymod.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    BooleanOption fixCooldownDesync = CookeyMod.getInstance().getConfig().getCategory(MiscCategory.class).fixCooldownDesync();

    private LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "drop", at = @At("RETURN"))
    public void resetAttackStrengthOnDrop(boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (fixCooldownDesync.get() && cir.getReturnValue())
            this.resetAttackStrengthTicker();
    }
}

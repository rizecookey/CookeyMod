package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.rizecookey.cookeymod.CookeyMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Final private Game game;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void injectUpdateCheck(GameConfig gameConfig, CallbackInfo ci) {
        CookeyMod.getInstance().getUpdater().checkForUpdates("rizecookey", "CookeyMod", this.game.getVersion().getId());
    }

    @Inject(method = "destroy", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE))
    public void injectApplyUpdate(CallbackInfo ci) {
        CookeyMod.getInstance().getUpdater().applyUpdate();
    }
}

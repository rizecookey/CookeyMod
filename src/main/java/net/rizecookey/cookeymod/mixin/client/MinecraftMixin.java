package net.rizecookey.cookeymod.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.rizecookey.cookeymod.update.ModUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "destroy", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE))
    public void injectUpdate(CallbackInfo ci) {
        // Unfinished
        /* ModUpdater.applyUpdate(FabricLoader.getInstance().getModContainer("cookeymod").get().getRootPath(),
                FabricLoader.getInstance().getGameDir().resolve("mods")); */
    }
}

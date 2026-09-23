package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Options;
import net.rizecookey.cookeymod.CookeyMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(method = "save", at = @At("TAIL"))
    private void saveModConfig(CallbackInfo ci) {
        CookeyMod.getInstance().getConfig().saveConfig();
    }
}

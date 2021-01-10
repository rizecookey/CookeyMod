package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Options;
import net.rizecookey.cookeymod.CookeyMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(method = "save", at = @At("TAIL"))
    public void saveModConfig(CallbackInfo ci) {
        try {
            CookeyMod.getInstance().getConfig().saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

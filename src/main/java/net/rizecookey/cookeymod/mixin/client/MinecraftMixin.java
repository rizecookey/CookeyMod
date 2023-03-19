package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import net.rizecookey.cookeymod.config.option.Option;
import net.rizecookey.cookeymod.extension.MultiPlayerGameModeExtension;
import net.rizecookey.cookeymod.extension.PlayerExtension;
import net.rizecookey.cookeymod.screen.ScreenBuilder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public MultiPlayerGameMode gameMode;
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    Option<Boolean> fixCooldownDesync;
    KeyMapping openCookeyModMenu;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initialize(GameConfig gameConfig, CallbackInfo ci) {
        CookeyMod cookeyMod = CookeyMod.getInstance();
        fixCooldownDesync = cookeyMod.getConfig().getCategory(MiscCategory.class).fixCooldownDesync;
        openCookeyModMenu = cookeyMod.getKeybinds().openOptions;
    }

    @Inject(method = "continueAttack", at = @At("TAIL"))
    public void runPendingResets(boolean bl, CallbackInfo ci) {
        if (this.fixCooldownDesync.get()) {
            assert this.player != null;
            assert this.gameMode != null;
            MultiPlayerGameModeExtension gameModeExt = ((MultiPlayerGameModeExtension) this.gameMode);

            if (gameModeExt.isAttackResetPending() && !this.gameMode.isDestroying()) {
                ((PlayerExtension) this.player).setAttackStrengthTicker(1);
            }

            gameModeExt.setAttackResetPending(false);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void openMenuOnKeyPress(CallbackInfo ci) {
        if (openCookeyModMenu.isDown() && this.screen == null) {
            this.setScreen(ScreenBuilder.buildConfig(null));
        }
    }
}

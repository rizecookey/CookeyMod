package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Game;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Overlay;
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
import net.rizecookey.cookeymod.util.Notifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public abstract ToastComponent getToasts();

    @Shadow
    public abstract Game getGame();

    @Shadow @Nullable public abstract Overlay getOverlay();

    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Nullable public Screen screen;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    private final Notifier updateNotifier = new Notifier();

    Option<Boolean> fixCooldownDesync;
    KeyMapping openCookeyModMenu;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void checkForUpdates(GameConfig gameConfig, CallbackInfo ci) {
        CookeyMod cookeyMod = CookeyMod.getInstance();
        fixCooldownDesync = cookeyMod.getConfig().getCategory(MiscCategory.class).fixCooldownDesync;
        openCookeyModMenu = cookeyMod.getKeybinds().openOptions;
        /* Disable Updater (will revamp this later)
        String user = "rizecookey", repo = "CookeyMod", branch = this.getGame().getVersion().getId();

        new Thread(() -> {
            if (cookeyMod.getUpdater().checkForUpdates(user, repo, branch)) {
                synchronized (this.getToasts()) {
                    try {
                        if (this.getOverlay() instanceof LoadingOverlay) {
                            synchronized (updateNotifier) {
                                updateNotifier.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.getToasts().addToast(new SystemToast(
                            SystemToast.SystemToastIds.TUTORIAL_HINT,
                            new TranslatableComponent("options.cookeymod.update.notification.title"),
                            new TranslatableComponent("options.cookeymod.update.notification.subtitle")));
                }
            }
        }).start();
         */
    }

    /*
    @Inject(method = "setOverlay", at = @At("HEAD"))
    public void notifyUpdateThread(Overlay overlay, CallbackInfo ci) {
        Overlay currentOverlay = this.getOverlay();
        if (currentOverlay instanceof LoadingOverlay) {
            synchronized (updateNotifier) {
                updateNotifier.notify();
            }
        }
    }

    @Inject(method = "destroy", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE))
    public void applyUpdates(CallbackInfo ci) {
        GitHubUpdater updater = CookeyMod.getInstance().getUpdater();
        if (updater.isUpdateDownloaded()) {
            updater.applyUpdate();
        }
    }
     */

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

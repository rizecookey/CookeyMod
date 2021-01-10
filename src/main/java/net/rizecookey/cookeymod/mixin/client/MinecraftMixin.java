package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.main.GameConfig;
import net.minecraft.network.chat.TranslatableComponent;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.update.GitHubUpdater;
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

    private final Notifier updateNotifier = new Notifier();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void checkForUpdates(GameConfig gameConfig, CallbackInfo ci) {
        CookeyMod cookeyMod = CookeyMod.getInstance();
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
    }

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
}

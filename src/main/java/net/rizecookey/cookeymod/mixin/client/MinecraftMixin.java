package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.world.phys.HitResult;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.extension.MinecraftExtension;
import net.rizecookey.cookeymod.screen.ScreenBuilder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftExtension {
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Shadow @Nullable public HitResult hitResult;

    @Unique
    private KeyMapping openCookeyModMenu;

    @Unique
    private boolean isHoldingDownOnBlock;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initialize(GameConfig gameConfig, CallbackInfo ci) {
        CookeyMod cookeyMod = CookeyMod.getInstance();
        openCookeyModMenu = cookeyMod.getKeybinds().openOptions();

        isHoldingDownOnBlock = false;
    }

    @Inject(method = "continueAttack", at = @At("RETURN"))
    private void setHoldingDownOnBlock(boolean bl, CallbackInfo ci) {
        isHoldingDownOnBlock = bl && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void openMenuOnKeyPress(CallbackInfo ci) {
        if (openCookeyModMenu.isDown() && this.screen == null) {
            this.setScreen(ScreenBuilder.buildConfig(null));
        }
    }

    @Override
    public boolean cookeyMod$isHoldingDownOnBlock() {
        return isHoldingDownOnBlock;
    }
}

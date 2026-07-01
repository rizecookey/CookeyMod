package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.extension.minecraft.MinecraftExtension;
import net.rizecookey.cookeymod.screen.ScreenBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftExtension {
    @Shadow
    @Final
    public Gui gui;
    @Unique
    private KeyMapping openCookeyModMenu;

    @Unique
    private boolean isHoldingDownOnBlock;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initialize(CallbackInfo ci) {
        CookeyMod cookeyMod = CookeyMod.getInstance();
        openCookeyModMenu = cookeyMod.getKeybinds().openOptions();

        isHoldingDownOnBlock = false;
    }

    @Inject(method = "continueAttack", at = @At("HEAD"))
    private void setDefault(CallbackInfo ci, @Share("isHoldingDownOnBlock") LocalBooleanRef isHoldingDownOnBlock) {
        isHoldingDownOnBlock.set(false);
    }

    @ModifyExpressionValue(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean setHoldingDownOnBlock(boolean original, @Share("isHoldingDownOnBlock") LocalBooleanRef isHoldingDownOnBlock) {
        isHoldingDownOnBlock.set(original);
        return original;
    }

    @Inject(method = "continueAttack", at = @At("RETURN"))
    private void setHoldingDownOnBlock(CallbackInfo ci, @Share("isHoldingDownOnBlock") LocalBooleanRef isHoldingDownOnBlock) {
        this.isHoldingDownOnBlock = isHoldingDownOnBlock.get();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void openMenuOnKeyPress(CallbackInfo ci) {
        if (openCookeyModMenu.isDown() && this.gui.screen() == null) {
            this.gui.setScreen(ScreenBuilder.buildConfig(null));
        }
    }

    @Override
    public boolean cookeyMod$isHoldingDownOnBlock() {
        return isHoldingDownOnBlock;
    }
}

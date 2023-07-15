package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.extension.MultiPlayerGameModeExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin implements MultiPlayerGameModeExtension {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private boolean attackResetPending;

    @Unique
    BooleanOption fixCooldownDesync = CookeyMod.getInstance().getConfig().misc().fixCooldownDesync();

    @Inject(method = "destroyBlock", at = @At("TAIL"))
    public void setTicksSinceDestroy(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (fixCooldownDesync.get())
            this.attackResetPending = true;
    }

    @Inject(method = {"useItem", "useItemOn", "interact", "interactAt"}, at = @At("RETURN"))
    public void interactResetAttackStrength(CallbackInfoReturnable<InteractionResult> cir) {
        if (fixCooldownDesync.get() && cir.getReturnValue() == InteractionResult.SUCCESS)
            this.resetAttackStrengthTicker();
    }

    @Unique
    public void resetAttackStrengthTicker() {
        assert this.minecraft.player != null;
        this.minecraft.player.resetAttackStrengthTicker();
    }

    @Override
    public boolean isAttackResetPending() {
        return attackResetPending;
    }

    @Override
    public void setAttackResetPending(boolean attackResetPending) {
        this.attackResetPending = attackResetPending;
    }
}

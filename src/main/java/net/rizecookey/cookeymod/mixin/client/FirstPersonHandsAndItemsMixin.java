package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.config.category.HudRenderingCategory;
import net.rizecookey.cookeymod.config.category.MiscCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItems.class)
public abstract class FirstPersonHandsAndItemsMixin {
    @Unique
    private HudRenderingCategory hudRenderingCategory;

    @Unique
    private MiscCategory miscCategory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectOptions(CallbackInfo ci) {
        ModConfig modConfig = CookeyMod.getInstance().getConfig();
        hudRenderingCategory = modConfig.hudRendering();
        miscCategory = modConfig.misc();
    }

    @ModifyVariable(method = "tick", slice = @Slice(
            from = @At(value = "JUMP", ordinal = 3)
    ), at = @At(value = "FIELD", ordinal = 0))
    private float modifyArmHeight(float f) {
        if (miscCategory.fixCooldownDesync().get() && Minecraft.getInstance().cookeyMod$isHoldingDownOnBlock()) {
            return 1.0f;
        }
        double offset = hudRenderingCategory.attackCooldownHandOffset().get();
        return (float) (f * (1 - offset) + offset);
    }
}

package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {
    @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void extractExtensions(LivingEntity livingEntity, HumanoidRenderState humanoidRenderState, float f, CallbackInfo ci) {
        humanoidRenderState.cookeyMod$setUseItemDuration(livingEntity.getUseItem().getUseDuration(livingEntity));

        humanoidRenderState.cookeyMod$setItemUseRemainingTicks(livingEntity.getUseItemRemainingTicks());
    }
}

package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {
    @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void extractExtensions(LivingEntity entity, HumanoidRenderState state, float partialTicks, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        state.cookeyMod$setUseItemDuration(entity.getUseItem().getUseDuration(entity));

        state.cookeyMod$setItemUseRemainingTicks(entity.getUseItemRemainingTicks());

        state.cookeyMod$setItemUseIsEating(entity.getUseItem().getUseAnimation() == ItemUseAnimation.EAT || entity.getUseItem().getUseAnimation() == ItemUseAnimation.DRINK);
    }
}

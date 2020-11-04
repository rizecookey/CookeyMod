package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.MiscOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected LivingEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
    public void showOwnName(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity == Minecraft.getInstance().cameraEntity
        && CookeyMod.getInstance().getConfig().getCategory(MiscOptions.class).isShowOwnNameInThirdPerson()) cir.setReturnValue(true);
    }
}

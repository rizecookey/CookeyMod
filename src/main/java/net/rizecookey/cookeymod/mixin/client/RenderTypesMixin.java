package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.rizecookey.cookeymod.extension.minecraft.RenderPipelinesExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(RenderTypes.class)
public abstract class RenderTypesMixin {
    @Shadow
    public static RenderType entityCutout(Identifier texture) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    public static RenderType entityTranslucent(Identifier texture) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Unique
    private static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL_OVERLAY = Util.memoize(
            texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelinesExtension.ARMOR_CUTOUT_NO_CULL_OVERLAY)
                        .withTexture("Sampler0", texture)
                        .useLightmap()
                        .useOverlay()
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .affectsCrumbling()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderTypeAccessor.invokeCreate("armor_cutout_no_cull_overlay", state);
            });

    @Inject(method = "armorCutoutNoCull", at = @At("HEAD"), cancellable = true)
    private static void useOverlayVariant(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(ARMOR_CUTOUT_NO_CULL_OVERLAY.apply(texture));
        cir.cancel();
    }

    @Inject(method = "itemCutout", at = @At("HEAD"), cancellable = true)
    private static void useEntityVariantForCutout(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(entityCutout(texture));
        cir.cancel();
    }

    @Inject(method = "itemTranslucent", at = @At("HEAD"), cancellable = true)
    private static void useEntityVariantForTranslucent(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(entityTranslucent(texture));
        cir.cancel();
    }
}

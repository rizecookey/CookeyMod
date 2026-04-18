package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.rizecookey.cookeymod.extension.minecraft.RenderPipelinesExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(RenderTypes.class)
public abstract class RenderTypesMixin {

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

    @Unique
    private static final Function<Identifier, RenderType> ITEM_CUTOUT_OVERLAY = Util.memoize(
            texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelinesExtension.ITEM_CUTOUT_OVERLAY)
                        .withTexture("Sampler0", texture)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderTypeAccessor.invokeCreate("item_cutout_overlay", state);
            }
    );

    @Unique
    private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_OVERLAY = Util.memoize(
            texture -> {
                RenderSetup state = RenderSetup.builder(RenderPipelinesExtension.ITEM_TRANSLUCENT_OVERLAY)
                        .withTexture("Sampler0", texture)
                        .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                        .useLightmap()
                        .useOverlay()
                        .affectsCrumbling()
                        .sortOnUpload()
                        .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                        .createRenderSetup();
                return RenderTypeAccessor.invokeCreate("item_translucent_overlay", state);
            }
    );

    @Inject(method = "armorCutoutNoCull", at = @At("HEAD"), cancellable = true)
    private static void useOverlayVariant(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(ARMOR_CUTOUT_NO_CULL_OVERLAY.apply(texture));
        cir.cancel();
    }

    @Inject(method = "itemCutout", at = @At("HEAD"), cancellable = true)
    private static void useEntityVariantForCutout(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(ITEM_CUTOUT_OVERLAY.apply(texture));
        cir.cancel();
    }

    @Inject(method = "itemTranslucent", at = @At("HEAD"), cancellable = true)
    private static void useEntityVariantForTranslucent(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(ITEM_TRANSLUCENT_OVERLAY.apply(texture));
        cir.cancel();
    }
}

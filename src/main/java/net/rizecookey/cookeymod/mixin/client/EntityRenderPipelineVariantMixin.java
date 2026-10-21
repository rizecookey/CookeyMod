package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import net.minecraft.client.renderer.pipeline.variant.EntityRenderPipelineVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(EntityRenderPipelineVariant.class)
public abstract class EntityRenderPipelineVariantMixin {
    @Unique
    private static Set<String> ARMOR_VARIANTS;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void initialize(CallbackInfo ci) {
        ARMOR_VARIANTS = Set.of("armor_cutout_no_cull", "armor_cutout_no_cull_glint", "armor_decal_cutout_no_cull");
    }

    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/pipeline/variant/EntityRenderPipelineVariant;"))
    private static EntityRenderPipelineVariant modifyTouseOverlay(String name, ColorTargetState nonOitColorTargetState, DepthStencilState nonOitDepthStencilState, EntityRenderPipelineVariant.Lighting lighting, boolean supportsOit, boolean hasOverlay, boolean hasAlphaCutout, boolean hasBackFaceCulling, boolean hasGlint, boolean isEmissive, boolean applyTextureMatrix, boolean dissolve, boolean additive, Operation<EntityRenderPipelineVariant> original) {
        if (ARMOR_VARIANTS.contains(name)) {
            hasOverlay = true;
        }

        return original.call(name, nonOitColorTargetState, nonOitDepthStencilState, lighting, supportsOit, hasOverlay, hasAlphaCutout, hasBackFaceCulling, hasGlint, isEmissive, applyTextureMatrix, dissolve, additive);
    }
}

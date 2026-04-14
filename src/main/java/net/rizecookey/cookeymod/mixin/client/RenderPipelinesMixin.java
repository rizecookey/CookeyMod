package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin {
    @Shadow
    @Final
    private static RenderPipeline.Snippet ENTITY_SNIPPET;

    @ModifyArg(method = "<clinit>", slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderPipelines;ARMOR_CUTOUT_NO_CULL:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", shift = At.Shift.BEFORE)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderPipelines;register(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)Lcom/mojang/blaze3d/pipeline/RenderPipeline;", ordinal = 0), index = 0)
    private static RenderPipeline redefineArmorCutoutNoCullWithOverlay(RenderPipeline value) {
        return RenderPipeline.builder(ENTITY_SNIPPET)
                .withLocation("pipeline/armor_cutout_no_cull")
                .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                .withShaderDefine("PER_FACE_LIGHTING")
                .withSampler("Sampler1")
                .withCull(false)
                .build();
    }
}

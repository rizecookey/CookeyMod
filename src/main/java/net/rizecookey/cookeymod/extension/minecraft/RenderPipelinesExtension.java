package net.rizecookey.cookeymod.extension.minecraft;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import static net.rizecookey.cookeymod.mixin.client.RenderPipelinesAccessor.getEntitySnippet;
import static net.rizecookey.cookeymod.mixin.client.RenderPipelinesAccessor.invokeRegister;

public interface RenderPipelinesExtension {
    RenderPipeline ARMOR_CUTOUT_NO_CULL_OVERLAY = invokeRegister(
            RenderPipeline.builder(getEntitySnippet())
                    .withLocation("pipeline/armor_cutout_no_cull_overlay")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withSampler("Sampler1")
                    .withCull(false)
                    .build()
    );
}

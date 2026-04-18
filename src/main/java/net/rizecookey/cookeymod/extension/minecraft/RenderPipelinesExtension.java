package net.rizecookey.cookeymod.extension.minecraft;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import static net.rizecookey.cookeymod.mixin.client.RenderPipelinesAccessor.getEntitySnippet;
import static net.rizecookey.cookeymod.mixin.client.RenderPipelinesAccessor.getItemSnippet;
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

    RenderPipeline ITEM_CUTOUT_OVERLAY = invokeRegister(
            RenderPipeline.builder(getItemSnippet())
                    .withLocation("pipeline/item_cutout_overlay")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withSampler("Sampler1")
                    .build());

    RenderPipeline ITEM_TRANSLUCENT_OVERLAY = invokeRegister(
            RenderPipeline.builder(getItemSnippet())
                    .withLocation("pipeline/item_translucent_overlay")
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withSampler("Sampler1")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .build());
}

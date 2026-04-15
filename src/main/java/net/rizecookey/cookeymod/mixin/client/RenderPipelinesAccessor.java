package net.rizecookey.cookeymod.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {
    @Accessor("ENTITY_SNIPPET")
    static RenderPipeline.Snippet getEntitySnippet() {
        throw new UnsupportedOperationException("Accessor not applied");
    }

    @Invoker
    static RenderPipeline invokeRegister(RenderPipeline renderPipeline) {
        throw new UnsupportedOperationException("Accessor not applied");
    }
}

package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin {
    @ModifyExpressionValue(method = "<clinit>", slice = @Slice(
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderPipelines;ARMOR_CUTOUT_NO_CULL:Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;", opcode = Opcodes.PUTSTATIC)
    ), at = @At(value = "INVOKE:LAST", target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;builder([Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Snippet;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;"))
    private static RenderPipeline.Builder setArmorCutoutNoCullBuilder(RenderPipeline.Builder original, @Share("builderToAddSamplerTo") LocalRef<RenderPipeline.Builder> builderToAddSamplerTo, @Share("armorCutoutNoCullBuilder") LocalRef<RenderPipeline.Builder> armorCutoutNoCullBuilder) {
        armorCutoutNoCullBuilder.set(original);
        builderToAddSamplerTo.set(original);
        return original;
    }

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;withShaderDefine(Ljava/lang/String;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;"))
    private static RenderPipeline.Builder preventNoOverlayDefineOnArmorCutoutNoCull(RenderPipeline.Builder instance, String key, Operation<RenderPipeline.Builder> original, @Share("armorCutoutNoCullBuilder") LocalRef<RenderPipeline.Builder> armorCutoutNoCullBuilder) {
        if (instance == armorCutoutNoCullBuilder.get() && key.equals("NO_OVERLAY")) {
            return instance;
        }

        return original.call(instance, key);
    }

    @ModifyReceiver(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;build()Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;"))
    private static RenderPipeline.Builder addOverlaySampler(RenderPipeline.Builder instance, @Share("builderToAddSamplerTo") LocalRef<RenderPipeline.Builder> builderToAddSamplerTo) {
        if (builderToAddSamplerTo.get() == instance) {
            return instance.withBindGroupLayout(BindGroupLayouts.SAMPLER1);
        }

        return instance;
    }
}

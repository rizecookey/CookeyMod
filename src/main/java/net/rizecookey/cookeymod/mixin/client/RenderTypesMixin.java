package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderTypes.class)
public abstract class RenderTypesMixin {
    @ModifyReceiver(method = "lambda$static$10", slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderPipelines;ITEM_CUTOUT:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.GETSTATIC),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderSetup$RenderSetupBuilder;createRenderSetup()Lnet/minecraft/client/renderer/rendertype/RenderSetup;")
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderSetup$RenderSetupBuilder;affectsCrumbling()Lnet/minecraft/client/renderer/rendertype/RenderSetup$RenderSetupBuilder;"))
    private static RenderSetup.RenderSetupBuilder enableOverlayForItemCutout(RenderSetup.RenderSetupBuilder instance) {
        return instance.useOverlay();
    }
}

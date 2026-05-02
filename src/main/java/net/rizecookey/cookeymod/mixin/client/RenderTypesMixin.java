package net.rizecookey.cookeymod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(RenderTypes.class)
public abstract class RenderTypesMixin {
    @Unique
    private static ScopedValue<Void> ADD_USE_OVERLAY;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void initializeScopedValue(CallbackInfo ci) {
        ADD_USE_OVERLAY = ScopedValue.newInstance();
    }

    @WrapOperation(method = "<clinit>", slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;ITEM_CUTOUT:Ljava/util/function/Function;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.BY, by = -1),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;ITEM_CUTOUT:Ljava/util/function/Function;", opcode = Opcodes.PUTSTATIC)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;"))
    private static Function<Identifier, RenderType> markUseOverlayItemCutout(Function<Identifier, RenderType> function, Operation<Function<Identifier, RenderType>> original) {
        return identifier -> ScopedValue.where(ADD_USE_OVERLAY, null).call(() -> original.call(function).apply(identifier));
    }

    @WrapOperation(method = "<clinit>", slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;ITEM_TRANSLUCENT:Ljava/util/function/Function;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.BY, by = -1),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;ITEM_TRANSLUCENT:Ljava/util/function/Function;", opcode = Opcodes.PUTSTATIC)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;"))
    private static Function<Identifier, RenderType> markUseOverlayItemTranslucent(Function<Identifier, RenderType> function, Operation<Function<Identifier, RenderType>> original) {
        return identifier -> ScopedValue.where(ADD_USE_OVERLAY, null).call(() -> original.call(function).apply(identifier));
    }

    @ModifyExpressionValue(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderSetup;builder(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)Lnet/minecraft/client/renderer/rendertype/RenderSetup$RenderSetupBuilder;"))
    private static RenderSetup.RenderSetupBuilder addUseOverlay(RenderSetup.RenderSetupBuilder original) {
        if (ADD_USE_OVERLAY.isBound()) {
            return original.useOverlay();
        }

        return original;
    }
}

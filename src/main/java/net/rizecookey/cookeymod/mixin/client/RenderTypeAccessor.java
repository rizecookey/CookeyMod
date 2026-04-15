package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface RenderTypeAccessor {
    @Invoker
    static RenderType invokeCreate(final String name, final RenderSetup state) {
        throw new UnsupportedOperationException("Accessor not applied");
    }
}

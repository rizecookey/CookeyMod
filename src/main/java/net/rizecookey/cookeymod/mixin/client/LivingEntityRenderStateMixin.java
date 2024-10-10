package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.rizecookey.cookeymod.extension.minecraft.LivingEntityRenderStateExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin extends EntityRenderState implements LivingEntityRenderStateExtension {
    @Override
    public LivingEntityRenderState cookeyMod$base() {
        return (LivingEntityRenderState) (Object) this;
    }
}

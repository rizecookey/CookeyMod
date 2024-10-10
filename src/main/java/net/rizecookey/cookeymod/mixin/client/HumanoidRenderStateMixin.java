package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.rizecookey.cookeymod.extension.minecraft.HumanoidRenderStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public abstract class HumanoidRenderStateMixin extends LivingEntityRenderState implements HumanoidRenderStateExtension {
    @Unique
    private int itemUseRemainingTicks;

    @Unique
    private int itemUseDuration;

    @Override
    public HumanoidRenderState cookeyMod$base() {
        return (HumanoidRenderState) (Object) this;
    }

    @Override
    public int cookeyMod$itemUseRemainingTicks() {
        return itemUseRemainingTicks;
    }

    @Override
    public void cookeyMod$setItemUseRemainingTicks(int ticks) {
        itemUseRemainingTicks = ticks;
    }

    @Override
    public int cookeyMod$useItemDuration() {
        return itemUseDuration;
    }

    @Override
    public void cookeyMod$setUseItemDuration(int ticks) {
        itemUseDuration = ticks;
    }
}

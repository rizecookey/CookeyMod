package net.rizecookey.cookeymod.extension.minecraft;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public interface HumanoidRenderStateExtension {
    default HumanoidRenderState cookeyMod$base() {
        throw new IllegalStateException("Extension has not been applied");
    }

    default HumanoidArm cookeyMod$getUsedArm() {
        return cookeyMod$base().useItemHand.equals(InteractionHand.MAIN_HAND) && cookeyMod$base().mainArm.equals(HumanoidArm.RIGHT)
                || cookeyMod$base().useItemHand.equals(InteractionHand.OFF_HAND) && cookeyMod$base().mainArm.equals(HumanoidArm.LEFT)
                ? HumanoidArm.RIGHT
                : HumanoidArm.LEFT;
    }

    default boolean cookeyMod$itemUseIsEating() {
        throw new IllegalStateException("Extension has not been applied");
    }
    default void cookeyMod$setItemUseIsEating(boolean useIsEating) {
        throw new IllegalStateException("Extension has not been applied");
    }

    default int cookeyMod$itemUseRemainingTicks() {
        throw new IllegalStateException("Extension has not been applied");
    }
    default void cookeyMod$setItemUseRemainingTicks(int ticks) {
        throw new IllegalStateException("Extension has not been applied");
    }

    default int cookeyMod$useItemDuration() {
        throw new IllegalStateException("Extension has not been applied");
    }
    default void cookeyMod$setUseItemDuration(int ticks) {
        throw new IllegalStateException("Extension has not been applied");
    }
}

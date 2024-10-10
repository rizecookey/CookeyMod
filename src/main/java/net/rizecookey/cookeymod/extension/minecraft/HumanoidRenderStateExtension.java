package net.rizecookey.cookeymod.extension.minecraft;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface HumanoidRenderStateExtension extends LivingEntityRenderStateExtension {
    default HumanoidRenderState cookeyMod$base() {
        throw new IllegalStateException("Extension has not been applied");
    }

    default HumanoidArm cookeyMod$getUsedArm() {
        return cookeyMod$base().useItemHand.equals(InteractionHand.MAIN_HAND) && cookeyMod$base().mainArm.equals(HumanoidArm.RIGHT)
                ? HumanoidArm.RIGHT
                : HumanoidArm.LEFT;
    }
    default ItemStack cookeyMod$getUsedItem() {
        return cookeyMod$base().useItemHand.equals(InteractionHand.MAIN_HAND)
                ? cookeyMod$base().getMainHandItem()
                : cookeyMod$getOffhandItem();
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

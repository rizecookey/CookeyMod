package net.rizecookey.cookeymod.extension.minecraft;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface LivingEntityRenderStateExtension {
    default LivingEntityRenderState cookeyMod$base() {
        throw new IllegalStateException("Extension has not been applied");
    }

    default ItemStack cookeyMod$getOffhandItem() {
        return cookeyMod$base().mainArm.equals(HumanoidArm.RIGHT)
                ? cookeyMod$base().leftHandItem
                : cookeyMod$base().rightHandItem;
    }

    default ItemStack cookeyMod$getItemInHand(InteractionHand hand) {
        return hand.equals(InteractionHand.MAIN_HAND)
                ? cookeyMod$base().getMainHandItem()
                : cookeyMod$getOffhandItem();
    }
}

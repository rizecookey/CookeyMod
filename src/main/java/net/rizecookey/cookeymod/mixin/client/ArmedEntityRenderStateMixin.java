package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShieldItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.option.BooleanOption;
import net.rizecookey.cookeymod.extension.minecraft.ArmedEntityRenderStateExtension;
import net.rizecookey.cookeymod.util.ItemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmedEntityRenderState.class)
public abstract class ArmedEntityRenderStateMixin implements ArmedEntityRenderStateExtension {
    @Unique
    private static final BooleanOption ENABLE_TOOL_BLOCKING = CookeyMod.getInstance().getConfig().animations().enableToolBlocking();

    @Unique
    private boolean poseBothArms;

    @Override
    public boolean cookeyMod$shouldPoseBothArms() {
        return poseBothArms;
    }

    @Override
    public void cookeyMod$setShouldPoseBothArms(boolean poseBothArms) {
        this.poseBothArms = poseBothArms;
    }

    @Inject(method = "extractArmedEntityRenderState", at = @At("RETURN"))
    private static void addToolBlocking(LivingEntity entity, ArmedEntityRenderState state, ItemModelResolver itemModelResolver, float partialTicks, CallbackInfo ci) {
        var otherHand = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        if (!ENABLE_TOOL_BLOCKING.get() || !(entity.getUseItem().getItem() instanceof ShieldItem) || !ItemUtils.isToolItem(entity.getItemInHand(otherHand).getItem())) {
            state.cookeyMod$setShouldPoseBothArms(false);
            return;
        }

        var usedArm = entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
        switch (usedArm) {
            case LEFT -> {
                state.leftHandItemState.clear();
                state.leftArmPose = HumanoidModel.ArmPose.EMPTY;
                state.rightArmPose = HumanoidModel.ArmPose.BLOCK;
            }
            case RIGHT -> {
                state.rightHandItemState.clear();
                state.rightArmPose = HumanoidModel.ArmPose.EMPTY;
                state.leftArmPose = HumanoidModel.ArmPose.BLOCK;
            }
        }
        state.cookeyMod$setShouldPoseBothArms(true);
    }
}

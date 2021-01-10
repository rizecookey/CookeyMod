package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRenderDispatcher entityRenderDispatcher, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(entityRenderDispatcher, entityModel, f);
    }

    private static final Option<Boolean> enableToolBlocking = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).enableToolBlocking;

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void addItemBlockPose(AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (enableToolBlocking.get()) {
            ItemStack currentHandStack = abstractClientPlayer.getItemInHand(interactionHand);
            ItemStack otherHandStack = interactionHand == InteractionHand.MAIN_HAND ? abstractClientPlayer.getItemInHand(InteractionHand.OFF_HAND) : abstractClientPlayer.getItemInHand(InteractionHand.MAIN_HAND);
            if (!abstractClientPlayer.getUseItem().isEmpty() && abstractClientPlayer.getUseItem().getItem() instanceof ShieldItem) {
                if (currentHandStack.getItem() instanceof TieredItem && otherHandStack.getItem() instanceof ShieldItem) {
                    cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
                } else if (currentHandStack.getItem() instanceof ShieldItem && otherHandStack.getItem() instanceof TieredItem) {
                    cir.setReturnValue(HumanoidModel.ArmPose.EMPTY);
                }
            }
        }
    }
}

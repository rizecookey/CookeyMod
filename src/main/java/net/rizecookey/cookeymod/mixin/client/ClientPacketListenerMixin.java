package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.rizecookey.cookeymod.CookeyMod;
import net.rizecookey.cookeymod.config.category.AnimationsCategory;
import net.rizecookey.cookeymod.config.option.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    private ClientLevel level;

    Option<Boolean> enableDamageCameraTilt = CookeyMod.getInstance().getConfig().getCategory(AnimationsCategory.class).enableDamageCameraTilt;

    @Inject(method = "handleSetEntityMotion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;lerpMotion(DDD)V"))
    public void injectHurtDir(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket, CallbackInfo ci) {
        Entity entity = this.level.getEntity(clientboundSetEntityMotionPacket.getId());
        if (entity instanceof LivingEntity livingEntity) {
            if (this.enableDamageCameraTilt.get()) {
                livingEntity.hurtDir = (float) (Mth.atan2(clientboundSetEntityMotionPacket.getZa() / 8000D, clientboundSetEntityMotionPacket.getXa() / 8000D) * 57.2957763671875D - (double) livingEntity.yHeadRot);
            }
        }
    }
}

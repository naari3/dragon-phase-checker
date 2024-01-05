package com.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

@Mixin(EnderDragonEntity.class)
public class MixinEnderDragonEntity {
    static boolean previousVerticalCollision = false;
    static boolean previousHorizontalCollision = false;

    // @Inject(method = "setPhase", at = @At("HEAD"))
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void beforeBeginPhase(CallbackInfo ci) {
        var enderDragon = (EnderDragonEntity) (Object) this;
        if (previousVerticalCollision != enderDragon.verticalCollision
                || previousHorizontalCollision != enderDragon.horizontalCollision) {
            previousVerticalCollision = enderDragon.verticalCollision;
            previousHorizontalCollision = enderDragon.horizontalCollision;
            var message = "verticalCollision: " + enderDragon.verticalCollision + ", horizontalCollision: "
                    + enderDragon.horizontalCollision;
            var mc = MinecraftClient.getInstance();
            mc.inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText(message), mc.player.getUuid());
        }

    }
}

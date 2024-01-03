package com.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

@Mixin(PhaseManager.class)
public class MixinPhaseManager {
	// @Inject(method = "setPhase", at = @At("HEAD"))
	@Inject(method = "setPhase", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;debug(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", shift = At.Shift.BEFORE))
	private void beforeBeginPhase(PhaseType<?> type, CallbackInfo ci) {
		System.out.println(type);
		var mc = MinecraftClient.getInstance();
		var message = "The dragon is now in phase " + type.toString();
		mc.inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText(message), mc.player.getUuid());
	}
}

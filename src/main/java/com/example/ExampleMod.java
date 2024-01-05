package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.world.Heightmap;

import static net.minecraft.server.command.CommandManager.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("showpathnodes")
					.executes(context -> {
						this.sendToChat("Howdy");
						context.getSource().getWorld().getAliveEnderDragons().forEach(enderDragon -> {
							this.sendToChat("Entity: " + enderDragon);
							var world = context.getSource().getWorld();
							var armorStands = world.getEntities(EntityType.ARMOR_STAND, e -> true);
							for (var armorStand : armorStands) {
								armorStand.remove();
							}

							var i = 0;
							for (PathNode pathNode : enderDragon.pathNodes) {
								this.sendToChat(
										"PathNode " + i + ": " + pathNode.x + ", " + pathNode.y + ", " + pathNode.z);
								// set beacon at pathNode
								var pos = pathNode.getPos();
								var topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);

								world.setBlockState(topPos.add(0, -2, 0), Blocks.BEACON.getDefaultState());
								// set iron block for beacon to shine through
								for (int j = -1; j < 2; j++) {
									for (int k = -1; k < 2; k++) {
										world.setBlockState(topPos.add(j, -3, k), Blocks.IRON_BLOCK.getDefaultState());
									}
								}
								var glassBlock = Blocks.WHITE_STAINED_GLASS;
								if (i < 12) {
									glassBlock = Blocks.WHITE_STAINED_GLASS;
								} else if (i < 20) {
									glassBlock = Blocks.LIGHT_BLUE_STAINED_GLASS;
								} else {
									glassBlock = Blocks.RED_STAINED_GLASS;
								}

								world.setBlockState(topPos.add(0, -1, 0), glassBlock.getDefaultState());

								var armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, world);
								armorStand.setInvisible(true);
								armorStand.setNoGravity(true);
								armorStand.setCustomNameVisible(true);
								armorStand.setCustomName(new LiteralText("PathNode " + i));

								armorStand.updatePosition(pos.getX() + 0.5, pos.getY() + 0.5,
										pos.getZ() + 0.5);

								world.spawnEntity(armorStand);

								i++;
							}
						});
						return 1;
					}));
			dispatcher.register(literal("showpathtarget")
					.executes(context -> {
						this.sendToChat("Howdy2");
						context.getSource().getWorld().getAliveEnderDragons().forEach(enderDragon -> {
							this.sendToChat("Entity: " + enderDragon);
							this.sendToChat("collisions: " + enderDragon.verticalCollision + ", "
									+ enderDragon.horizontalCollision);
							var target = enderDragon.phaseManager.getCurrent().getTarget();
							this.sendToChat("Target: " + target);
						});
						return 1;
					}));
		});
	}

	private void sendToChat(String message) {
		var mc = MinecraftClient.getInstance();
		mc.inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText(message), mc.player.getUuid());
	}
}
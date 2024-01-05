package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

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
								world.setBlockState(pos, Blocks.BEACON.getDefaultState());
								// set iron block for beacon to shine through
								for (int j = -1; j < 2; j++) {
									for (int k = -1; k < 2; k++) {
										world.setBlockState(pos.add(j, -1, k), Blocks.IRON_BLOCK.getDefaultState());
									}
								}

								var lowGlassBlock = this.getGlassBlockFromIndex(i % 16);
								world.setBlockState(pos.add(0, 1, 0), lowGlassBlock.getDefaultState());
								if (i > 15) {
									var highGlassBlock = this.getGlassBlockFromIndex((i + 1) % 16);
									world.setBlockState(pos.add(0, 2, 0), highGlassBlock.getDefaultState());
								}

								var armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, world);
								armorStand.setInvisible(true);
								armorStand.setNoGravity(true);
								armorStand.setCustomNameVisible(true);
								armorStand.setCustomName(new LiteralText("PathNode " + i));

								armorStand.updatePosition(pos.getX() + 0.5, pos.getY() + 1.5,
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

	private Block getGlassBlockFromIndex(int i) {
		var glassBlock = Blocks.WHITE_STAINED_GLASS;
		switch (i) {
			case 0:
				glassBlock = Blocks.WHITE_STAINED_GLASS;
				break;
			case 1:
				glassBlock = Blocks.ORANGE_STAINED_GLASS;
				break;
			case 2:
				glassBlock = Blocks.MAGENTA_STAINED_GLASS;
				break;
			case 3:
				glassBlock = Blocks.LIGHT_BLUE_STAINED_GLASS;
				break;
			case 4:
				glassBlock = Blocks.YELLOW_STAINED_GLASS;
				break;
			case 5:
				glassBlock = Blocks.LIME_STAINED_GLASS;
				break;
			case 6:
				glassBlock = Blocks.PINK_STAINED_GLASS;
				break;
			case 7:
				glassBlock = Blocks.GRAY_STAINED_GLASS;
				break;
			case 8:
				glassBlock = Blocks.LIGHT_GRAY_STAINED_GLASS;
				break;
			case 9:
				glassBlock = Blocks.CYAN_STAINED_GLASS;
				break;
			case 10:
				glassBlock = Blocks.PURPLE_STAINED_GLASS;
				break;
			case 11:
				glassBlock = Blocks.BLUE_STAINED_GLASS;
				break;
			case 12:
				glassBlock = Blocks.BROWN_STAINED_GLASS;
				break;
			case 13:
				glassBlock = Blocks.GREEN_STAINED_GLASS;
				break;
			case 14:
				glassBlock = Blocks.RED_STAINED_GLASS;
				break;
			case 15:
				glassBlock = Blocks.BLACK_STAINED_GLASS;
				break;
		}
		return glassBlock;
	}
}
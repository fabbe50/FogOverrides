package com.fabbe50.fogoverrides.commands;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class CommandFogOverrides {
    public static void registerCommands() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("fogoverrides")
                    .then(LiteralArgumentBuilder.<CommandSourceStack>literal("distance")
                            .then(RequiredArgumentBuilder.argument("near", IntegerArgumentType.integer()))
                            .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("far", IntegerArgumentType.integer())
                                    .executes(context -> {
                                                final int nearDistance = IntegerArgumentType.getInteger(context, "near");
                                                final int farDistance = IntegerArgumentType.getInteger(context, "far");
                                                if (context.getSource().getEntity() instanceof Player player) {
                                                    Level level = player.level();
                                                    Optional<ResourceKey<Biome>> biomeKey = level.getBiome(player.blockPosition()).unwrapKey();
                                                    if (biomeKey.isPresent()) {
                                                        ResourceLocation location = biomeKey.get().location();
                                                        ModFogData fogData = ModConfig.getFogDataFromBiomeLocation(location.toString());
                                                        fogData.setNearDistance(nearDistance);
                                                        fogData.setFarDistance(farDistance);
                                                        ModConfig.updateFogData(location, fogData);
                                                        player.sendSystemMessage(Component.literal("Successfully updated fog distances for biome: " + location));
                                                    }
                                                } else {
                                                    CommandSource source = (CommandSource) context.getSource();
                                                    source.sendSystemMessage(Component.literal("Could not update fog distances, source is not a player.").withStyle(ChatFormatting.RED));
                                                }
                                                return farDistance - nearDistance;
                                            }
                                    )))
            );
        });
    }
}

package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.*;
import com.fabbe50.fogoverrides.data.*;
import com.fabbe50.fogoverrides.network.interfaces.*;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandler {
    public static List<Player> modUsers = new ArrayList<>();

    public static void registerHandlers() {
        new SettingsSavedUpdateAllClientsPacket().registerServer();
        new HandshakePacket().registerServer();
        new GameModeSettingsPacket().registerServer();
        new LiquidsPacket().registerServer();
        new CloudsPacket().registerServer();
        new OverlaysPacket().registerServer();
        new FogSettingsPacket().registerServer();
    }

    public static void registerClientHandlers() {
        new SettingsSavedUpdateAllClientsPacket().registerClient();
        new HandshakePacket().registerClient();
        new GameModeSettingsPacket().registerClient();
        new LiquidsPacket().registerClient();
        new CloudsPacket().registerClient();
        new OverlaysPacket().registerClient();
        new FogSettingsPacket().registerClient();
        new OpenFogSettingsPacket().registerClient();
        new RegistryPacket().registerClient();
    }

    public static void registerClientHandshake() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(localPlayer -> {
            if (localPlayer.is(ClientUtilities.getClientPlayer())) {
                try {
                    sendToServer(new HandshakePacket(), true);
                } catch (UnsupportedOperationException e) {
                    System.out.println("Server does not have Fog Overrides installed.");
                }
            }
        });
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (player == null || player.is(ClientUtilities.getClientPlayer())) {
                CurrentDataStorage.INSTANCE.setOnFogOverridesEnabledServer(false);
                CurrentDataStorage.INSTANCE.setIntegratedServer(false);
            }
        });
    }

    public static void registerServerHandshake() {
        PlayerEvent.PLAYER_QUIT.register(player -> {
            modUsers.remove(player);
        });
    }

    public static void openConfigScreenOnClient(ServerPlayer player) {
        sendSettingsToPlayer(player);
        sendToPlayer(player, new OpenFogSettingsPacket());
    }

    public static void sendSettingsToServer(ModConfig config) {
        sendToServer(new GameModeSettingsPacket(), new ConfigPair<>("spectator", config.spectatorSettings));
        sendToServer(new GameModeSettingsPacket(), new ConfigPair<>("creative", config.creativeSettings));
        for (ResourceLocation location : ModRegistry.getDimensions()) {
            ModFogData fogData = config.getFogDataFromDimension(location);
            if (fogData != null) {
                sendToServer(new FogSettingsPacket(), new ConfigPair<>(location, fogData));
            }
        }
        for (ResourceLocation location : ModRegistry.getBiomes()) {
            ModFogData fogData = config.getFogDataFromBiomeLocation(location.toString());
            if (fogData != null) {
                sendToServer(new FogSettingsPacket(), new ConfigPair<>(location, fogData));
            }
        }
        sendToServer(new LiquidsPacket(), config);
        sendToServer(new CloudsPacket(), config);
        sendToServer(new OverlaysPacket(), config);
        sendToServer(new SettingsSavedUpdateAllClientsPacket(), 0);
    }

    public static <T> void sendToServer(IDataPacket<T, ?> sidedPacket, T data) {
        NetworkManager.sendToServer(sidedPacket.getServerPacketID(), sidedPacket.getDefaultPayload().write(getNewBuffer(), data));
    }

    public static int sendSettingsToAllPlayers() {
        int i = 0;
        for (Player player : modUsers) {
            if (player != null) {
                sendSettingsToPlayer(player);
                i++;
            }
        }
        return i;
    }

    public static void sendSettingsToPlayer(Player player) {
        Log.info("Sending settings to player: " + player.getName());
        sendToPlayer(player, new GameModeSettingsPacket(), new ConfigPair<>("spectator", ModConfig.INSTANCE.spectatorSettings));
        sendToPlayer(player, new GameModeSettingsPacket(), new ConfigPair<>("creative", ModConfig.INSTANCE.creativeSettings));
        for (ResourceLocation location : ModRegistry.getDimensions()) {
            ModFogData fogData = ModConfig.INSTANCE.getFogDataFromDimension(location);
            if (fogData != null) {
                sendToPlayer(player, new FogSettingsPacket(), new ConfigPair<>(location, fogData));
            }
        }
        for (ResourceLocation location : ModRegistry.getBiomes()) {
            ModFogData fogData = ModConfig.INSTANCE.getFogDataFromBiomeLocation(location.toString());
            if (fogData != null) {
                sendToPlayer(player, new FogSettingsPacket(), new ConfigPair<>(location, fogData));
            }
        }
        sendToPlayer(player, new LiquidsPacket(), ModConfig.INSTANCE);
        sendToPlayer(player, new CloudsPacket(), ModConfig.INSTANCE);
        sendToPlayer(player, new OverlaysPacket(), ModConfig.INSTANCE);
    }

    public static void sendToPlayer(Player player, IPacket<?> packet) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, packet.getClientPacketID(), getNewBuffer());
        }
    }

    public static <T> void sendToPlayer(Player player, IDataPacket<T, ?> sidedPacket, T data) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, sidedPacket.getClientPacketID(), sidedPacket.getDefaultPayload().write(getNewBuffer(), data));
        }
    }

    public static FriendlyByteBuf getNewBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }
}

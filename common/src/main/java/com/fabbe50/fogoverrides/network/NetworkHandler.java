package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ClientUtilities;
import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.*;
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
        SettingsSavedUpdateAllClientsPacket.Server.register();
        HandshakePacket.ServerPacket.Server.register();
        GameModeSettingsPacket.Server.register();
        LiquidsPacket.Server.register();
        CloudsPacket.Server.register();
        OverlaysPacket.Server.register();
        FogSettingsPacket.Server.register();
    }

    public static void registerServerHandlers() {
        SettingsSavedUpdateAllClientsPacket.Client.registerServer();
        GameModeSettingsPacket.Client.registerServer();
        LiquidsPacket.Client.registerServer();
        CloudsPacket.Client.registerServer();
        OverlaysPacket.Client.registerServer();
        FogSettingsPacket.Client.registerServer();
        HandshakePacket.ClientPacket.registerServer();
        OpenFogSettingsPacket.register();
        RegistryPacket.Client.registerServer();
    }

    public static void registerClientHandlers() {
        SettingsSavedUpdateAllClientsPacket.Client.register();
        OpenFogSettingsPacket.Client.register();
        HandshakePacket.ClientPacket.Client.register();
        GameModeSettingsPacket.Client.register();
        LiquidsPacket.Client.register();
        CloudsPacket.Client.register();
        OverlaysPacket.Client.register();
        FogSettingsPacket.Client.register();
        RegistryPacket.Client.register();
    }

    public static void registerClientHandshake() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(localPlayer -> {
            if (localPlayer.is(ClientUtilities.getClientPlayer())) {
                try {
                    NetworkManager.sendToServer(new HandshakePacket.ServerPacket.PacketPayload(true));
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
        NetworkManager.sendToPlayer(player, new OpenFogSettingsPacket.PacketPayload());
    }

    public static void sendSettingsToServer(ModConfig config) {
        NetworkManager.sendToServer(new GameModeSettingsPacket.Server.PacketPayload(getGameModeSettingsBuffer("spectator", config.spectatorSettings)));
        NetworkManager.sendToServer(new GameModeSettingsPacket.Server.PacketPayload(getGameModeSettingsBuffer("creative", config.creativeSettings)));
        for (ResourceLocation location : ModRegistry.getDimensions()) {
            ModFogData fogData = config.getFogDataFromDimension(location);
            if (fogData != null) {
                NetworkManager.sendToServer(new FogSettingsPacket.Server.PacketPayload(location, fogData));
            }
        }
        for (ResourceLocation location : ModRegistry.getBiomes()) {
            ModFogData fogData = config.getFogDataFromBiomeLocation(location.toString());
            if (fogData != null) {
                NetworkManager.sendToServer(new FogSettingsPacket.Server.PacketPayload(location, fogData));
            }
        }
        NetworkManager.sendToServer(new LiquidsPacket.Server.PacketPayload(getLiquidBuffer(config)));
        NetworkManager.sendToServer(new CloudsPacket.Server.PacketPayload(getCloudBuffer(config)));
        NetworkManager.sendToServer(new OverlaysPacket.Server.PacketPayload(getOverlaysBuffer(config)));
        NetworkManager.sendToServer(new SettingsSavedUpdateAllClientsPacket.Server.PacketPayload(0));
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

    public static void refreshConfigFileAndSendSettingsToPlayer(Player player) {
        ModConfig.load(ModConfig.getConfigFile());
        sendSettingsToPlayer(player);
    }

    public static void sendSettingsToPlayer(Player player) {
        Log.info("Sending settings to player: " + player.getName());
        NetworkManager.sendToPlayer((ServerPlayer) player, new GameModeSettingsPacket.Client.PacketPayload(getGameModeSettingsBuffer("spectator", ModConfig.INSTANCE.spectatorSettings)));
        NetworkManager.sendToPlayer((ServerPlayer) player, new GameModeSettingsPacket.Client.PacketPayload(getGameModeSettingsBuffer("creative", ModConfig.INSTANCE.creativeSettings)));
        for (ResourceLocation location : ModRegistry.getDimensions()) {
            ModFogData fogData = ModConfig.INSTANCE.getFogDataFromDimension(location);
            if (fogData != null) {
                NetworkManager.sendToPlayer((ServerPlayer) player, new FogSettingsPacket.Client.PacketPayload(location, fogData));
            }
        }
        for (String location : ModConfig.INSTANCE.getBiomeStorage().keySet()) {
            ModFogData fogData = ModConfig.INSTANCE.getFogDataFromBiomeLocation(location);
            if (fogData != null) {
                NetworkManager.sendToPlayer((ServerPlayer) player, new FogSettingsPacket.Client.PacketPayload(ResourceLocation.parse(location), fogData));
            }
        }
        NetworkManager.sendToPlayer((ServerPlayer) player, new LiquidsPacket.Client.PacketPayload(getLiquidBuffer(ModConfig.INSTANCE)));
        NetworkManager.sendToPlayer((ServerPlayer) player, new CloudsPacket.Client.PacketPayload(getCloudBuffer(ModConfig.INSTANCE)));
        NetworkManager.sendToPlayer((ServerPlayer) player, new OverlaysPacket.Client.PacketPayload(getOverlaysBuffer(ModConfig.INSTANCE)));
    }

    public static FriendlyByteBuf createNewBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static FriendlyByteBuf getGameModeSettingsBuffer(String gameMode, GameModeSettings settings) {
        FriendlyByteBuf buf = createNewBuffer();
        buf.writeUtf(gameMode);
        settings.writeBuffer(buf);
        return buf;
    }

    public static FriendlyByteBuf getLiquidBuffer(ModConfig config) {
        FriendlyByteBuf buf = createNewBuffer();
        buf.writeBoolean(config.waterFogEnabled);
        buf.writeBoolean(config.lavaFogEnabled);
        return buf;
    }

    public static FriendlyByteBuf getCloudBuffer(ModConfig config) {
        FriendlyByteBuf buf = createNewBuffer();
        buf.writeInt(config.cloudHeight);
        return buf;
    }

    public static FriendlyByteBuf getOverlaysBuffer(ModConfig config) {
        FriendlyByteBuf buf = createNewBuffer();
        buf.writeBoolean(config.renderWaterOverlay);
        buf.writeBoolean(config.renderFireOverlay);
        buf.writeInt(config.fireOverlayOffset);
        buf.writeInt(config.firePotionOverlayOffset);
        return buf;
    }
}

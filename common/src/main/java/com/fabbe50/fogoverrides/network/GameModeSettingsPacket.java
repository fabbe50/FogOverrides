package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;
import com.fabbe50.fogoverrides.network.interfaces.*;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

public class GameModeSettingsPacket implements IDataPacket<ConfigPair<String, GameModeSettings>, GameModeSettingsPacket.GameModeSettingsPayload> {
    @Override
    public String getPacketID() {
        return "gamemode";
    }

    @Override
    public void receiveClient(GameModeSettingsPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received game mode settings from server: " + payload);
            String gameMode = payload.gameMode();
            String fogMode = payload.fogMode();
            boolean terrainFog = payload.terrainFog();
            float nearDistance = payload.nearDistance();
            float farDistance = payload.farDistance();
            FogSetting terrain = new FogSetting(terrainFog, nearDistance, farDistance);
            boolean waterFog = payload.waterFog();
            float waterNear = payload.waterNear();
            float waterFar = payload.waterFar();
            FogSetting water = new FogSetting(waterFog, waterNear, waterFar);
            boolean lavaFog = payload.lavaFog();
            float lavaNear = payload.lavaNear();
            float lavaFar = payload.lavaFar();
            FogSetting lava = new FogSetting(lavaFog, lavaNear, lavaFar);
            GameModeSettings settings = new GameModeSettings(GameModeSettings.FogMode.getModeFromID(fogMode), terrain, water, lava);

            CurrentDataStorage.INSTANCE.updateGameModeSettings(gameMode, settings);
        });
    }

    @Override
    public void receiveServer(GameModeSettingsPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer().hasPermissions(4)) {
                Log.info("Received game mode settings from admin client: " + payload);
                String gameMode = payload.gameMode();
                GameModeSettings settings = payload.readGameModeSettings();
                if (gameMode.equals("creative")) {
                    ModConfig.INSTANCE.creativeSettings = settings;
                } else if (gameMode.equals("spectator")) {
                    ModConfig.INSTANCE.spectatorSettings = settings;
                }
            }
        });
    }

    @Override
    public GameModeSettingsPayload getPayload(FriendlyByteBuf buf) {
        return new GameModeSettingsPayload(buf);
    }

    @Override
    public GameModeSettingsPayload getDefaultPayload() {
        return new GameModeSettingsPayload("undefined", Utilities.getDefaultGameModeSettings());
    }

    public record GameModeSettingsPayload(String gameMode, String fogMode, boolean terrainFog, float nearDistance, float farDistance, boolean waterFog, float waterNear, float waterFar, boolean lavaFog, float lavaNear, float lavaFar) implements IDataPayload<ConfigPair<String, GameModeSettings>, GameModeSettingsPayload> {
        private GameModeSettingsPayload(String id, GameModeSettings settings) {
            this(settings.writeBuffer(new FriendlyByteBuf(Unpooled.buffer()).writeUtf(id)));
        }

        public GameModeSettingsPayload(FriendlyByteBuf buf) {
            this(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, ConfigPair<String, GameModeSettings> config) {
            buf.writeUtf(config.id());
            GameModeSettings settings = config.value();
            settings.writeBuffer(buf);
            return buf;
        }

        @Override
        public GameModeSettingsPayload read(FriendlyByteBuf buf) {
            return new GameModeSettingsPayload(buf);
        }

        public FogSetting readTerrainFog() {
            return new FogSetting(terrainFog, nearDistance, farDistance);
        }

        public FogSetting readWaterFog() {
            return new FogSetting(waterFog, waterNear, waterFar);
        }

        public FogSetting readLavaFog() {
            return new FogSetting(lavaFog, lavaNear, lavaFar);
        }

        public GameModeSettings readGameModeSettings() {
            return new GameModeSettings(GameModeSettings.FogMode.getModeFromID(fogMode), readTerrainFog(), readWaterFog(), readLavaFog());
        }
    }
}

package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.GameModeSettings;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GameModeSettingsPacket {
    private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "gamemode_fog");
    private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

    public static Packet<ClientGamePacketListener> create(String gameMode, String fogMode, boolean terrainFog, float nearDistance, float farDistance, boolean waterFog, float waterNear, float waterFar, boolean lavaFog, float lavaNear, float lavaFar) {
        return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.s2c(), new PacketPayload(gameMode, fogMode, terrainFog, nearDistance, farDistance, waterFog, waterNear, waterFar, lavaFog, lavaNear, lavaFar), null);
    }

    public static void register() {
        NetworkManager.registerS2CPayloadType(PACKET_TYPE, PACKET_CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        @Environment(EnvType.CLIENT)
        public static void register() {
            NetworkManager.registerReceiver(NetworkManager.s2c(), PACKET_TYPE, PACKET_CODEC, Client::receive);
        }

        @Environment(EnvType.CLIENT)
        private static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
            context.queue(() -> {
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
    }

    public record PacketPayload(String gameMode, String fogMode, boolean terrainFog, float nearDistance, float farDistance, boolean waterFog, float waterNear, float waterFar, boolean lavaFog, float lavaNear, float lavaFar) implements CustomPacketPayload {
        public PacketPayload(FriendlyByteBuf buf) {
            this(buf.readUtf(), buf.readUtf(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(gameMode);
            buf.writeUtf(fogMode);
            buf.writeBoolean(terrainFog);
            buf.writeFloat(nearDistance);
            buf.writeFloat(farDistance);
            buf.writeBoolean(waterFog);
            buf.writeFloat(waterNear);
            buf.writeFloat(waterFar);
            buf.writeBoolean(lavaFog);
            buf.writeFloat(lavaNear);
            buf.writeFloat(lavaFar);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
}

package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.data.CurrentDataStorage;
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

public class CreativeSettingsPacket {
    private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "creative_fog");
    private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

    public static Packet<ClientGamePacketListener> create(boolean hasModFog, float nearDistance, float farDistance, float waterNear, float waterFar, float lavaNear, float lavaFar) {
        return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.s2c(), new PacketPayload(hasModFog, nearDistance, farDistance, waterNear, waterFar, lavaNear, lavaFar), null);
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
                boolean hasModFog = payload.hasModFog();
                float nearDistance = payload.nearDistance();
                float farDistance = payload.farDistance();
                float waterNear = payload.waterNear();
                float waterFar = payload.waterFar();
                float lavaNear = payload.lavaNear();
                float lavaFar = payload.lavaFar();
                CurrentDataStorage.INSTANCE.updateCreativeSettings(hasModFog, nearDistance, farDistance, waterNear, waterFar, lavaNear, lavaFar);
            });
        }
    }

    public record PacketPayload(boolean hasModFog, float nearDistance, float farDistance, float waterNear, float waterFar, float lavaNear, float lavaFar) implements CustomPacketPayload {
        public PacketPayload(FriendlyByteBuf buf) {
            this(buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeBoolean(hasModFog);
            buf.writeFloat(nearDistance);
            buf.writeFloat(farDistance);
            buf.writeFloat(waterNear);
            buf.writeFloat(waterFar);
            buf.writeFloat(lavaNear);
            buf.writeFloat(lavaFar);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
}

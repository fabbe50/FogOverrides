package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class LiquidsPacket {
    public static class Client {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "client_liquids");
        private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
        private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

        public static void registerServer() {
            NetworkManager.registerS2CPayloadType(PACKET_TYPE, PACKET_CODEC);
        }

        @Environment(EnvType.CLIENT)
        public static void register() {
            NetworkManager.registerReceiver(NetworkManager.s2c(), PACKET_TYPE, PACKET_CODEC, Client::receive);
        }

        @Environment(EnvType.CLIENT)
        private static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
            context.queue(() -> {
                Log.info("Received liquid settings from server: " + payload);
                boolean waterFog = payload.waterFog();
                boolean lavaFog = payload.lavaFog();
                CurrentDataStorage.INSTANCE.updateLiquids(waterFog, lavaFog);
            });
        }

        public record PacketPayload(boolean waterFog, boolean lavaFog) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readBoolean(), buf.readBoolean());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeBoolean(waterFog);
                buf.writeBoolean(lavaFog);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }

    public static class Server {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "server_liquids");
        private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
        private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

        public static void register() {
            NetworkManager.registerReceiver(NetworkManager.c2s(), PACKET_TYPE, PACKET_CODEC, Server::receive);
        }

        private static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
            context.queue(() -> {
                if (context.getPlayer().hasPermissions(4)) {
                    Log.info("Received liquid settings from admin client: " + payload);
                    ModConfig.INSTANCE.waterFogEnabled = payload.waterFog();
                    ModConfig.INSTANCE.lavaFogEnabled = payload.lavaFog();
                }
            });
        }

        public record PacketPayload(boolean waterFog, boolean lavaFog) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readBoolean(), buf.readBoolean());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeBoolean(waterFog);
                buf.writeBoolean(lavaFog);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }
}

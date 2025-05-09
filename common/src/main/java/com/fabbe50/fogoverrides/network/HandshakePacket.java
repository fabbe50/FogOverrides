package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ClientUtilities;
import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModRegistry;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;


public class HandshakePacket {
    public static class ServerPacket {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "server_handshake");
        private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
        private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

        public static Packet<ClientGamePacketListener> create(boolean isModEnabledServer) {
            return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.c2s(), new PacketPayload(isModEnabledServer), null);
        }

        public static void register() {
            NetworkManager.registerS2CPayloadType(PACKET_TYPE, PACKET_CODEC);
        }

        public static class Server {
            public static void register() {
                NetworkManager.registerReceiver(NetworkManager.c2s(), PACKET_TYPE, PACKET_CODEC, Server::receive);
            }

            private static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
                context.queue(() -> {
                    Log.info("Received handshake from client with payload: " + payload);
                    if (payload.modEnabledServer()) {
                        if (!NetworkHandler.modUsers.contains(context.getPlayer()))
                            NetworkHandler.modUsers.add(context.getPlayer());
                        NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new HandshakePacket.ClientPacket.PacketPayload(true));
                        NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new RegistryPacket.Client.PacketPayload(ModRegistry.getDimensions(), ModRegistry.getBiomes()));
                        NetworkHandler.sendSettingsToPlayer(context.getPlayer());
                    }
                });
            }
        }

        public record PacketPayload(boolean modEnabledServer) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readBoolean());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeBoolean(modEnabledServer);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }

        public static CustomPacketPayload.Type<PacketPayload> getPacketType() {
            return PACKET_TYPE;
        }

        public static StreamCodec<FriendlyByteBuf, PacketPayload> getPacketCodec() {
            return PACKET_CODEC;
        }
    }

    public static class ClientPacket {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "client_handshake");
        private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
        private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

        public static Packet<ClientGamePacketListener> create(boolean isModEnabledServer) {
            return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.s2c(), new PacketPayload(isModEnabledServer), null);
        }

        public static void registerServer() {
            NetworkManager.registerS2CPayloadType(PACKET_TYPE, PACKET_CODEC);
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
                    Log.info("Received handshake packet from server with payload: " + payload);
                    if (ClientUtilities.isIntegratedServer()) {
                        CurrentDataStorage.INSTANCE.setIntegratedServer(true);
                    }
                    CurrentDataStorage.INSTANCE.setOnFogOverridesEnabledServer(payload.modEnabledServer());
                });
            }
        }

        public record PacketPayload(boolean modEnabledServer) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readBoolean());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeBoolean(modEnabledServer);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }

        public static CustomPacketPayload.Type<PacketPayload> getPacketType() {
            return PACKET_TYPE;
        }

        public static StreamCodec<FriendlyByteBuf, PacketPayload> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}

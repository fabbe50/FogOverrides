package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SettingsSavedUpdateAllClientsPacket {
    public static class Client {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "client_update_settings");
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
                Log.info("Received save ping confirmation from server.");
                int i = payload.i();
                if (i == 1) {
                    context.getPlayer().displayClientMessage(Component.translatable("text.fogoverrides.information.updated-players.single", payload.i()), false);
                } else {
                    context.getPlayer().displayClientMessage(Component.translatable("text.fogoverrides.information.updated-players.multi", payload.i()), false);
                }
            });
        }

        public record PacketPayload(int i) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readInt());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeInt(i);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }

    public static class Server {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "server_update_settings");
        private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
        private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

        public static void register() {
            NetworkManager.registerReceiver(NetworkManager.c2s(), PACKET_TYPE, PACKET_CODEC, Server::receive);
        }

        private static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
            context.queue(() -> {
                if (context.getPlayer().hasPermissions(4)) {
                    Log.info("Received save ping from admin client.");
                    try {
                        ModConfig.save(ModConfig.getConfigFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ModConfig.load(ModConfig.getConfigFile());

                    int updatedPlayers = NetworkHandler.sendSettingsToAllPlayers();
                    if (updatedPlayers > 0) {
                        NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new Client.PacketPayload(updatedPlayers));
                    }
                }
            });
        }

        public record PacketPayload(int i) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readInt());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeInt(i);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }
}

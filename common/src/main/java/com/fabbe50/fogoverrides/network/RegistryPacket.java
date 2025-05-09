package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.ModRegistry;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegistryPacket {
    public static class Client {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "client_registry_packet");
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
                Log.info("Received registries from server: " + payload);
                for (ResourceLocation dimension : payload.dimensions()) {
                    if (dimension != null) {
                        ModRegistry.addDimensionToList(dimension);
                    }
                }
                for (ResourceLocation biome : payload.biomes()) {
                    if (biome != null) {
                        ModRegistry.addBiomeToList(biome);
                    }
                }
                ModConfig.loadMainConfig();
            });
        }

        public record PacketPayload(List<ResourceLocation> dimensions, List<ResourceLocation> biomes) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readList(FriendlyByteBuf::readResourceLocation), buf.readList(FriendlyByteBuf::readResourceLocation));
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeCollection(dimensions, FriendlyByteBuf::writeResourceLocation);
                buf.writeCollection(biomes, FriendlyByteBuf::writeResourceLocation);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }
}

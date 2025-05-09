package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.ModRegistry;
import com.fabbe50.fogoverrides.network.interfaces.ConfigPair;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RegistryPacket implements IDataPacket<ConfigPair<List<ResourceLocation>, List<ResourceLocation>>, RegistryPacket.RegistryPacketPayload> {
    @Override
    public String getPacketID() {
        return "registry";
    }

    @Override
    public void receiveClient(RegistryPacketPayload payload, NetworkManager.PacketContext context) {
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

    @Override
    public void receiveServer(RegistryPacketPayload payload, NetworkManager.PacketContext context) {

    }

    @Override
    public RegistryPacketPayload getPayload(FriendlyByteBuf buf) {
        return new RegistryPacketPayload(buf);
    }

    @Override
    public RegistryPacketPayload getDefaultPayload() {
        return new RegistryPacketPayload(ModRegistry.getDimensions(), ModRegistry.getBiomes());
    }

    public record RegistryPacketPayload(List<ResourceLocation> dimensions, List<ResourceLocation> biomes) implements IDataPayload<ConfigPair<List<ResourceLocation>, List<ResourceLocation>>, RegistryPacketPayload> {
        public RegistryPacketPayload(FriendlyByteBuf buf) {
            this(
                    buf.readList(FriendlyByteBuf::readResourceLocation),
                    buf.readList(FriendlyByteBuf::readResourceLocation)
            );
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, ConfigPair<List<ResourceLocation>, List<ResourceLocation>> registryPair) {
            buf.writeCollection(registryPair.id(), FriendlyByteBuf::writeResourceLocation);
            buf.writeCollection(registryPair.value(), FriendlyByteBuf::writeResourceLocation);
            return buf;
        }

        @Override
        public RegistryPacketPayload read(FriendlyByteBuf buf) {
            return new RegistryPacketPayload(buf);
        }
    }
}

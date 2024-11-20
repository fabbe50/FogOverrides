package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
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

public class DimensionSettingsPacket {
    private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "dimension_fog");
    private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

    public static Packet<ClientGamePacketListener> create(ResourceLocation location, ModFogData fogData) {
        return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.s2c(), new PacketPayload(location, fogData), null);
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
                ResourceLocation location = payload.location();
                boolean overrideBiomeFog = payload.overrideFog();
                boolean biomeFogEnabled = payload.fogEnabled();
                int nearDistance = payload.nearDistance();
                int farDistance = payload.farDistance();
                boolean overrideSkyColor = payload.overrideSkyColor();
                int skyColor = payload.skyColor();
                boolean overrideFogColor = payload.overrideFogColor();
                int fogColor = payload.fogColor();
                boolean overrideWaterFog = payload.overrideWaterFog();
                int waterNear = payload.waterNear();
                int waterFar = payload.waterFar();
                boolean waterPotionEffect = payload.waterPotionEffect();
                int waterPotionNear = payload.waterPotionNear();
                int waterPotionFar = payload.waterPotionFar();
                boolean overrideWaterFogColor = payload.overrideWaterFogColor();
                int waterFogColor = payload.waterFogColor();
                boolean overrideLavaFog = payload.overrideLavaFog();
                int lavaNear = payload.lavaNear();
                int lavaFar = payload.lavaFar();
                boolean lavaPotionEffect = payload.lavaPotionEffect();
                int lavaPotionNear = payload.lavaPotionNear();
                int lavaPotionFar = payload.lavaPotionFar();
                ModFogData fogData = new ModFogData(overrideBiomeFog, biomeFogEnabled, nearDistance, farDistance,
                        overrideSkyColor, skyColor, overrideFogColor, fogColor,
                        overrideWaterFog, waterNear, waterFar, overrideWaterFogColor, waterFogColor);
                fogData.setWaterPotionEffect(waterPotionEffect);
                fogData.setWaterPotionNearDistance(waterPotionNear);
                fogData.setWaterPotionFarDistance(waterPotionFar);
                fogData.setOverrideLavaFog(overrideLavaFog);
                fogData.setLavaNearDistance(lavaNear);
                fogData.setLavaFarDistance(lavaFar);
                fogData.setLavaPotionEffect(lavaPotionEffect);
                fogData.setLavaPotionNearDistance(lavaPotionNear);
                fogData.setLavaPotionFarDistance(lavaPotionFar);
                if (location.equals(Utilities.getOverworld())) {
                    CurrentDataStorage.INSTANCE.updateOverworldFogData(fogData);
                } else if (location.equals(Utilities.getNether())) {
                    CurrentDataStorage.INSTANCE.updateNetherFogData(fogData);
                } else if (location.equals(Utilities.getTheEnd())) {
                    CurrentDataStorage.INSTANCE.updateTheEndFogData(fogData);
                }
            });
        }
    }

    public record PacketPayload(ResourceLocation location, boolean overrideFog, boolean fogEnabled, int nearDistance, int farDistance,
                                 boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                                 boolean overrideWaterFog, int waterNear, int waterFar, boolean waterPotionEffect, int waterPotionNear, int waterPotionFar,
                                 boolean overrideWaterFogColor, int waterFogColor,
                                 boolean overrideLavaFog, int lavaNear, int lavaFar, boolean lavaPotionEffect, int lavaPotionNear, int lavaPotionFar) implements CustomPacketPayload {
        public PacketPayload(FriendlyByteBuf buf) {
            this(buf.readResourceLocation(), buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt(),
                    buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                    buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readInt(), buf.readInt(),
                    buf.readBoolean(), buf.readInt(),
                    buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readInt(), buf.readInt());
        }

        public PacketPayload(ResourceLocation location, ModFogData modFogData) {
            this(location, modFogData.isOverrideGameFog(), modFogData.isFogEnabled(), (int) modFogData.getNearDistance(), (int) modFogData.getFarDistance(),
                    modFogData.isOverrideSkyColor(), modFogData.getSkyColor(), modFogData.isOverrideFogColor(), modFogData.getFogColor(),
                    modFogData.isOverrideWaterFog(), (int) modFogData.getWaterNearDistance(), (int) modFogData.getWaterFarDistance(), modFogData.isWaterPotionEffect(), (int) modFogData.getWaterPotionNearDistance(), (int) modFogData.getWaterPotionFarDistance(),
                    modFogData.isOverrideWaterFogColor(), modFogData.getWaterFogColor(),
                    modFogData.isOverrideLavaFog(), (int) modFogData.getLavaNearDistance(), (int) modFogData.getLavaFarDistance(), modFogData.isLavaPotionEffect(), (int) modFogData.getLavaPotionNearDistance(), (int) modFogData.getLavaPotionFarDistance());
        }

        public void write(FriendlyByteBuf buf) {
            ModFogData fogData = ModConfig.getFogDataFromDimension(location);
            if (fogData != null) {
                buf.writeResourceLocation(location);
                buf.writeBoolean(fogData.isOverrideGameFog());
                buf.writeBoolean(fogData.isFogEnabled());
                buf.writeInt((int) fogData.getNearDistance());
                buf.writeInt((int) fogData.getFarDistance());
                buf.writeBoolean(fogData.isOverrideSkyColor());
                buf.writeInt(fogData.getSkyColor());
                buf.writeBoolean(fogData.isOverrideFogColor());
                buf.writeInt(fogData.getFogColor());
                buf.writeBoolean(fogData.isOverrideWaterFog());
                buf.writeInt((int) fogData.getWaterNearDistance());
                buf.writeInt((int) fogData.getWaterFarDistance());
                buf.writeBoolean(fogData.isWaterPotionEffect());
                buf.writeInt((int) fogData.getWaterPotionNearDistance());
                buf.writeInt((int) fogData.getWaterPotionFarDistance());
                buf.writeBoolean(fogData.isOverrideWaterFogColor());
                buf.writeInt(fogData.getWaterFogColor());
                buf.writeBoolean(fogData.isOverrideLavaFog());
                buf.writeInt((int) fogData.getLavaNearDistance());
                buf.writeInt((int) fogData.getLavaFarDistance());
                buf.writeBoolean(fogData.isLavaPotionEffect());
                buf.writeInt((int) fogData.getLavaPotionNearDistance());
                buf.writeInt((int) fogData.getLavaPotionFarDistance());
            }
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
}

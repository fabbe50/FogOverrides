package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
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
                float nearDistance = payload.nearDistance();
                float farDistance = payload.farDistance();
                boolean overrideSkyColor = payload.overrideSkyColor();
                int skyColor = payload.skyColor();
                boolean overrideFogColor = payload.overrideFogColor();
                int fogColor = payload.fogColor();
                boolean overrideWaterFog = payload.overrideWaterFog();
                float waterNear = payload.waterNear();
                float waterFar = payload.waterFar();
                boolean waterPotionEffect = payload.waterPotionEffect();
                float waterPotionNear = payload.waterPotionNear();
                float waterPotionFar = payload.waterPotionFar();
                boolean overrideWaterFogColor = payload.overrideWaterFogColor();
                int waterFogColor = payload.waterFogColor();
                boolean overrideLavaFog = payload.overrideLavaFog();
                float lavaNear = payload.lavaNear();
                float lavaFar = payload.lavaFar();
                boolean lavaPotionEffect = payload.lavaPotionEffect();
                float lavaPotionNear = payload.lavaPotionNear();
                float lavaPotionFar = payload.lavaPotionFar();
                boolean rainEnabled = payload.rainEnabled();
                float rainNear = payload.rainNear();
                float rainFar = payload.rainFar();
                int rainColor = payload.rainColor();
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
                fogData.setRain(new FogSetting(rainEnabled, rainNear, rainFar, rainColor));
                if (location.getPath().equals(Utilities.getOverworld().getPath())) {
                    CurrentDataStorage.INSTANCE.updateOverworldFogData(fogData);
                } else if (location.getPath().equals(Utilities.getNether().getPath())) {
                    CurrentDataStorage.INSTANCE.updateNetherFogData(fogData);
                } else if (location.getPath().equals(Utilities.getTheEnd().getPath())) {
                    CurrentDataStorage.INSTANCE.updateTheEndFogData(fogData);
                }
            });
        }
    }

    public record PacketPayload(ResourceLocation location, boolean overrideFog, boolean fogEnabled, float nearDistance, float farDistance,
                                 boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                                 boolean overrideWaterFog, float waterNear, float waterFar, boolean waterPotionEffect, float waterPotionNear, float waterPotionFar,
                                 boolean overrideWaterFogColor, int waterFogColor,
                                 boolean overrideLavaFog, float lavaNear, float lavaFar, boolean lavaPotionEffect, float lavaPotionNear, float lavaPotionFar,
                                boolean rainEnabled, float rainNear, float rainFar, int rainColor) implements CustomPacketPayload {
        public PacketPayload(FriendlyByteBuf buf) {
            this(buf.readResourceLocation(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                    buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                    buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                    buf.readBoolean(), buf.readInt(),
                    buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                    buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }

        public PacketPayload(ResourceLocation location, ModFogData modFogData) {
            this(location, modFogData.isOverrideGameFog(), modFogData.isFogEnabled(), modFogData.getNearDistance(), modFogData.getFarDistance(),
                    modFogData.isOverrideSkyColor(), modFogData.getSkyColor(), modFogData.isOverrideFogColor(), modFogData.getFogColor(),
                    modFogData.isOverrideWaterFog(), modFogData.getWaterNearDistance(), modFogData.getWaterFarDistance(), modFogData.isWaterPotionEffect(), modFogData.getWaterPotionNearDistance(), modFogData.getWaterPotionFarDistance(),
                    modFogData.isOverrideWaterFogColor(), modFogData.getWaterFogColor(),
                    modFogData.isOverrideLavaFog(), modFogData.getLavaNearDistance(), modFogData.getLavaFarDistance(), modFogData.isLavaPotionEffect(), modFogData.getLavaPotionNearDistance(), modFogData.getLavaPotionFarDistance(),
                    modFogData.getRain().isEnabled(), modFogData.getRain().getNearDistance(), modFogData.getRain().getFarDistance(), modFogData.getRain().getColor());
        }

        public void write(FriendlyByteBuf buf) {
            ModFogData fogData = ModConfig.getFogDataFromDimension(location);
            if (fogData != null) {
                buf.writeResourceLocation(location);
                buf.writeBoolean(fogData.isOverrideGameFog());
                buf.writeBoolean(fogData.isFogEnabled());
                buf.writeFloat(fogData.getNearDistance());
                buf.writeFloat(fogData.getFarDistance());
                buf.writeBoolean(fogData.isOverrideSkyColor());
                buf.writeInt(fogData.getSkyColor());
                buf.writeBoolean(fogData.isOverrideFogColor());
                buf.writeInt(fogData.getFogColor());
                buf.writeBoolean(fogData.isOverrideWaterFog());
                buf.writeFloat(fogData.getWaterNearDistance());
                buf.writeFloat(fogData.getWaterFarDistance());
                buf.writeBoolean(fogData.isWaterPotionEffect());
                buf.writeFloat(fogData.getWaterPotionNearDistance());
                buf.writeFloat(fogData.getWaterPotionFarDistance());
                buf.writeBoolean(fogData.isOverrideWaterFogColor());
                buf.writeInt(fogData.getWaterFogColor());
                buf.writeBoolean(fogData.isOverrideLavaFog());
                buf.writeFloat(fogData.getLavaNearDistance());
                buf.writeFloat(fogData.getLavaFarDistance());
                buf.writeBoolean(fogData.isLavaPotionEffect());
                buf.writeFloat(fogData.getLavaPotionNearDistance());
                buf.writeFloat(fogData.getLavaPotionFarDistance());
                buf.writeBoolean(fogData.getRain().isEnabled());
                buf.writeFloat(fogData.getRain().getNearDistance());
                buf.writeFloat(fogData.getRain().getFarDistance());
                buf.writeInt(fogData.getRain().getColor());
            }
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
}

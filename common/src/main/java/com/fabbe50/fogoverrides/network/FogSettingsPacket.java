package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.fabbe50.fogoverrides.data.ModRegistry;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FogSettingsPacket {
    public static class Client {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "client_fog_settings");
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
                Log.info("Received fog settings from server: " + payload);
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
                boolean overrideWaterColor = payload.overrideWaterColor();
                int waterColor = payload.waterColor();
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
                        overrideWaterFog, waterNear, waterFar, overrideWaterColor, waterColor, overrideWaterFogColor, waterFogColor);
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
                if (ModRegistry.getDimensions().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                    CurrentDataStorage.INSTANCE.addToDimensionStorage(location, fogData);
                } else if (ModRegistry.getBiomes().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                    CurrentDataStorage.INSTANCE.addToBiomeStorage(location, fogData);
                    CurrentDataStorage.INSTANCE.refreshWaterColor(location, fogData);
                }
            });
        }

        public record PacketPayload(ResourceLocation location, boolean overrideFog, boolean fogEnabled, float nearDistance, float farDistance,
                                    boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                                    boolean overrideWaterFog, float waterNear, float waterFar, boolean waterPotionEffect, float waterPotionNear, float waterPotionFar,
                                    boolean overrideWaterColor, int waterColor, boolean overrideWaterFogColor, int waterFogColor,
                                    boolean overrideLavaFog, float lavaNear, float lavaFar, boolean lavaPotionEffect, float lavaPotionNear, float lavaPotionFar,
                                    boolean rainEnabled, float rainNear, float rainFar, int rainColor) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readResourceLocation(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                        buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                        buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                        buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                        buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                        buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readInt());
            }

            public PacketPayload(ResourceLocation location, ModFogData modFogData) {
                this(location, modFogData.isOverrideGameFog(), modFogData.isFogEnabled(), modFogData.getNearDistance(), modFogData.getFarDistance(),
                        modFogData.isOverrideSkyColor(), modFogData.getSkyColor(), modFogData.isOverrideFogColor(), modFogData.getFogColor(),
                        modFogData.isOverrideWaterFog(), modFogData.getWaterNearDistance(), modFogData.getWaterFarDistance(), modFogData.isWaterPotionEffect(),  modFogData.getWaterPotionNearDistance(),  modFogData.getWaterPotionFarDistance(),
                        modFogData.isOverrideWaterColor(), modFogData.getWaterColor(), modFogData.isOverrideWaterFogColor(), modFogData.getWaterFogColor(),
                        modFogData.isOverrideLavaFog(),  modFogData.getLavaNearDistance(),  modFogData.getLavaFarDistance(), modFogData.isLavaPotionEffect(),  modFogData.getLavaPotionNearDistance(),  modFogData.getLavaPotionFarDistance(),
                        modFogData.getRain().isEnabled(), modFogData.getRain().getNearDistance(), modFogData.getRain().getFarDistance(), modFogData.getRain().getColor());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeResourceLocation(location);
                buf.writeBoolean(overrideFog);
                buf.writeBoolean(fogEnabled);
                buf.writeFloat(nearDistance);
                buf.writeFloat(farDistance);
                buf.writeBoolean(overrideSkyColor);
                buf.writeInt(skyColor);
                buf.writeBoolean(overrideFogColor);
                buf.writeInt(fogColor);
                buf.writeBoolean(overrideWaterFog);
                buf.writeFloat(waterNear);
                buf.writeFloat(waterFar);
                buf.writeBoolean(waterPotionEffect);
                buf.writeFloat(waterPotionNear);
                buf.writeFloat(waterPotionFar);
                buf.writeBoolean(overrideWaterColor);
                buf.writeInt(waterColor);
                buf.writeBoolean(overrideWaterFogColor);
                buf.writeInt(waterFogColor);
                buf.writeBoolean(overrideLavaFog);
                buf.writeFloat(lavaNear);
                buf.writeFloat(lavaFar);
                buf.writeBoolean(lavaPotionEffect);
                buf.writeFloat(lavaPotionNear);
                buf.writeFloat(lavaPotionFar);
                buf.writeBoolean(rainEnabled);
                buf.writeFloat(rainNear);
                buf.writeFloat(rainFar);
                buf.writeInt(rainColor);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }

    public static class Server {
        private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "server_fog_settings");
        private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
        private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

        public static void register() {
            NetworkManager.registerReceiver(NetworkManager.c2s(), PACKET_TYPE, PACKET_CODEC, Server::receive);
        }

        private static void receive(PacketPayload payload, NetworkManager.PacketContext context) {
            context.queue(() -> {
                if (context.getPlayer().getPermissionLevel() == 4) {
                    Log.info("Received fog settings from admin client: " + payload);
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
                    boolean overrideWaterColor = payload.overrideWaterColor();
                    int waterColor = payload.waterColor();
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
                            overrideWaterFog, waterNear, waterFar, overrideWaterColor, waterColor, overrideWaterFogColor, waterFogColor);
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
                    if (ModRegistry.getDimensions().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                        ModConfig.INSTANCE.putDimensionInStorage(location, fogData);
                    } else if (ModRegistry.getBiomes().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                        ModConfig.INSTANCE.putBiomeInStorage(location, fogData);
                    }
                }
            });
        }

        public record PacketPayload(ResourceLocation location, boolean overrideFog, boolean fogEnabled, float nearDistance, float farDistance,
                                    boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                                    boolean overrideWaterFog, float waterNear, float waterFar, boolean waterPotionEffect, float waterPotionNear, float waterPotionFar,
                                    boolean overrideWaterColor, int waterColor, boolean overrideWaterFogColor, int waterFogColor,
                                    boolean overrideLavaFog, float lavaNear, float lavaFar, boolean lavaPotionEffect, float lavaPotionNear, float lavaPotionFar,
                                    boolean rainEnabled, float rainNear, float rainFar, int rainColor) implements CustomPacketPayload {
            public PacketPayload(FriendlyByteBuf buf) {
                this(buf.readResourceLocation(), buf.readBoolean(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                        buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                        buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                        buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                        buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                        buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readInt());
            }

            public PacketPayload(ResourceLocation location, ModFogData modFogData) {
                this(location, modFogData.isOverrideGameFog(), modFogData.isFogEnabled(), modFogData.getNearDistance(), modFogData.getFarDistance(),
                        modFogData.isOverrideSkyColor(), modFogData.getSkyColor(), modFogData.isOverrideFogColor(), modFogData.getFogColor(),
                        modFogData.isOverrideWaterFog(), modFogData.getWaterNearDistance(), modFogData.getWaterFarDistance(), modFogData.isWaterPotionEffect(),  modFogData.getWaterPotionNearDistance(),  modFogData.getWaterPotionFarDistance(),
                        modFogData.isOverrideWaterColor(), modFogData.getWaterColor(), modFogData.isOverrideWaterFogColor(), modFogData.getWaterFogColor(),
                        modFogData.isOverrideLavaFog(),  modFogData.getLavaNearDistance(),  modFogData.getLavaFarDistance(), modFogData.isLavaPotionEffect(),  modFogData.getLavaPotionNearDistance(),  modFogData.getLavaPotionFarDistance(),
                        modFogData.getRain().isEnabled(), modFogData.getRain().getNearDistance(), modFogData.getRain().getFarDistance(), modFogData.getRain().getColor());
            }

            public void write(FriendlyByteBuf buf) {
                buf.writeResourceLocation(location);
                buf.writeBoolean(overrideFog);
                buf.writeBoolean(fogEnabled);
                buf.writeFloat(nearDistance);
                buf.writeFloat(farDistance);
                buf.writeBoolean(overrideSkyColor);
                buf.writeInt(skyColor);
                buf.writeBoolean(overrideFogColor);
                buf.writeInt(fogColor);
                buf.writeBoolean(overrideWaterFog);
                buf.writeFloat(waterNear);
                buf.writeFloat(waterFar);
                buf.writeBoolean(waterPotionEffect);
                buf.writeFloat(waterPotionNear);
                buf.writeFloat(waterPotionFar);
                buf.writeBoolean(overrideWaterColor);
                buf.writeInt(waterColor);
                buf.writeBoolean(overrideWaterFogColor);
                buf.writeInt(waterFogColor);
                buf.writeBoolean(overrideLavaFog);
                buf.writeFloat(lavaNear);
                buf.writeFloat(lavaFar);
                buf.writeBoolean(lavaPotionEffect);
                buf.writeFloat(lavaPotionNear);
                buf.writeFloat(lavaPotionFar);
                buf.writeBoolean(rainEnabled);
                buf.writeFloat(rainNear);
                buf.writeFloat(rainFar);
                buf.writeInt(rainColor);
            }

            @Override
            public @NotNull Type<? extends CustomPacketPayload> type() {
                return PACKET_TYPE;
            }
        }
    }
}

package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.FogSetting;
import com.fabbe50.fogoverrides.data.ModFogData;
import com.fabbe50.fogoverrides.data.Registry;
import com.fabbe50.fogoverrides.network.interfaces.ConfigPair;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class FogSettingsPacket implements IDataPacket<ConfigPair<ResourceLocation, ModFogData>, FogSettingsPacket.FogSettingsPayload> {
    @Override
    public String getPacketID() {
        return "fog_settings";
    }

    @Override
    public void receiveClient(FogSettingsPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received fog settings from server: " + payload);
            ResourceLocation location = payload.location();
            ModFogData fogData = payload.readFogData();

            if (Registry.getDimensions().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                CurrentDataStorage.INSTANCE.addToDimensionStorage(location, fogData);
            } else if (Registry.getBiomes().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                CurrentDataStorage.INSTANCE.addToBiomeStorage(location, fogData);
                CurrentDataStorage.INSTANCE.refreshWaterColor(location, fogData);
            }
        });
    }

    @Override
    public void receiveServer(FogSettingsPayload payload, NetworkManager.PacketContext context) {
        if (context.getPlayer().hasPermissions(4)) {
            Log.info("Received fog settings from admin client: " + payload);
            ResourceLocation location = payload.location();
            ModFogData fogData = payload.readFogData();

            if (Registry.getDimensions().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                ModConfig.INSTANCE.putDimensionInStorage(location, fogData);
            } else if (Registry.getBiomes().stream().anyMatch(location1 -> location1.toString().equals(location.toString()))) {
                ModConfig.INSTANCE.putBiomeInStorage(location, fogData);
            }
        }
    }

    @Override
    public FogSettingsPayload getPayload(FriendlyByteBuf buf) {
        return new FogSettingsPayload(buf);
    }

    @Override
    public FogSettingsPayload getDefaultPayload() {
        return new FogSettingsPayload(new ResourceLocation("undefined"), Utilities.getDefaultFogData());
    }

    public record FogSettingsPayload(ResourceLocation location, boolean overrideFog, boolean fogEnabled, float nearDistance, float farDistance,
                                     boolean overrideSkyColor, int skyColor, boolean overrideFogColor, int fogColor,
                                     boolean overrideWaterFog, float waterNear, float waterFar, boolean waterPotionEffect, float waterPotionNear, float waterPotionFar,
                                     boolean overrideWaterColor, int waterColor, boolean overrideWaterFogColor, int waterFogColor,
                                     boolean overrideLavaFog, float lavaNear, float lavaFar, boolean lavaPotionEffect, float lavaPotionNear, float lavaPotionFar,
                                     boolean rainEnabled, float rainNear, float rainFar, int rainColor) implements IDataPayload<ConfigPair<ResourceLocation, ModFogData>, FogSettingsPayload> {
        public FogSettingsPayload(ResourceLocation location, ModFogData fogData) {
            this(fogData.writeBuffer(new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(location)));
        }

        public FogSettingsPayload(FriendlyByteBuf buf) {
            this(
                    buf.readResourceLocation(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt()
            );
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, ConfigPair<ResourceLocation, ModFogData> config) {
            buf.writeResourceLocation(config.id());
            ModFogData fogData = config.value();
            fogData.writeBuffer(buf);
            return buf;
        }

        @Override
        public FogSettingsPayload read(FriendlyByteBuf buf) {
            return new FogSettingsPayload(buf);
        }

        public ModFogData readFogData() {
            ModFogData fogData = new ModFogData(overrideFog, fogEnabled, nearDistance, farDistance,
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
            return fogData;
        }
    }
}

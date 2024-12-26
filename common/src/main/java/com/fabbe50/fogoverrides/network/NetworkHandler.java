package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.FogOverrides;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.data.ModFogData;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class NetworkHandler {
    public static List<Player> modUsers = new ArrayList<>();

    public static void registerHandlers() {
        S2CHandshakePacket.Client.register();
        SpectatorSettingsPacket.Client.register();
        CreativeSettingsPacket.Client.register();
        CloudsPacket.Client.register();
        OverlaysPacket.Client.register();
        DimensionSettingsPacket.Client.register();
        BiomeSettingsPacket.Client.register();
    }

    public static void registerClientHandshake() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(localPlayer -> {
            if (localPlayer.is(Utilities.getClientPlayer())) {
                try {
                    NetworkManager.sendToServer(new C2SHandshakePacket.PacketPayload(true));
                } catch (UnsupportedOperationException e) {
                    System.out.println("Server does not have Fog Overrides installed.");
                }
            }
        });
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (player == null || player.is(Utilities.getClientPlayer())) {
                CurrentDataStorage.INSTANCE.setOnFogOverridesEnabledServer(false);
                CurrentDataStorage.INSTANCE.setIntegratedServer(false);
            }
        });
    }

    public static void registerServerHandshake() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SHandshakePacket.getPacketType(), C2SHandshakePacket.getPacketCodec(), (value, context) -> {
            if (value.modEnabledServer()) {
                if (!modUsers.contains(context.getPlayer()))
                    modUsers.add(context.getPlayer());
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new S2CHandshakePacket.PacketPayload(true));
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new SpectatorSettingsPacket.PacketPayload(getSpectatorSettingsBuffer()));
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new CreativeSettingsPacket.PacketPayload(getCreativeSettingsBuffer()));
                ResourceLocation[] dimensionLocations = new ResourceLocation[] {Utilities.getOverworld(), Utilities.getNether(), Utilities.getTheEnd()};
                for (ResourceLocation location : dimensionLocations) {
                    ModFogData fogData = ModConfig.getFogDataFromDimension(location);
                    if (fogData != null) {
                        NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new DimensionSettingsPacket.PacketPayload(location, fogData));
                    }
                }
                for (String location : ModConfig.getBiomeStorage().keySet()) {
                    ModFogData fogData = ModConfig.getFogDataFromBiomeLocation(location);
                    if (fogData != null) {
                        NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new BiomeSettingsPacket.PacketPayload(ResourceLocation.parse(location), fogData));
                    }
                }
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new CloudsPacket.PacketPayload(getCloudBuffer()));
                NetworkManager.sendToPlayer((ServerPlayer) context.getPlayer(), new OverlaysPacket.PacketPayload(getOverlaysBuffer()));
            }
        });
        PlayerEvent.PLAYER_QUIT.register(player -> {
            modUsers.remove(player);
        });
    }

    public static FriendlyByteBuf getSpectatorSettingsBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ModConfig.spectatorHasModFog);
        buf.writeFloat(ModConfig.spectatorNearDistance);
        buf.writeFloat(ModConfig.spectatorFarDistance);
        buf.writeFloat(ModConfig.spectatorWaterNearDistance);
        buf.writeFloat(ModConfig.spectatorWaterFarDistance);
        buf.writeFloat(ModConfig.spectatorLavaNearDistance);
        buf.writeFloat(ModConfig.spectatorLavaFarDistance);
        return buf;
    }

    public static FriendlyByteBuf getCreativeSettingsBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ModConfig.creativeHasModFog);
        buf.writeFloat(ModConfig.creativeNearDistance);
        buf.writeFloat(ModConfig.creativeFarDistance);
        buf.writeFloat(ModConfig.creativeWaterNearDistance);
        buf.writeFloat(ModConfig.creativeWaterFarDistance);
        buf.writeFloat(ModConfig.creativeLavaNearDistance);
        buf.writeFloat(ModConfig.creativeLavaFarDistance);
        return buf;
    }

    public static FriendlyByteBuf getCloudBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(ModConfig.cloudHeight);
        return buf;
    }

    public static FriendlyByteBuf getOverlaysBuffer() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ModConfig.renderWaterOverlay);
        buf.writeBoolean(ModConfig.renderFireOverlay);
        buf.writeInt(ModConfig.fireOverlayOffset);
        buf.writeInt(ModConfig.firePotionOverlayOffset);
        return buf;
    }
}

package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ClothScreen;
import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OpenFogSettingsPacket {
    private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "fogsettings");
    private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

    public static Packet<ClientGamePacketListener> create() {
        return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.s2c(), new PacketPayload(), null);
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
                Log.info("Received open settings packet from server!");
                Minecraft.getInstance().setScreen(ClothScreen.getConfigScreen(null, CurrentDataStorage.INSTANCE.getServerSettings(), null));
            });
        }
    }

    public record PacketPayload() implements CustomPacketPayload {
        public PacketPayload(FriendlyByteBuf buf) {
            this();
        }

        public void write(FriendlyByteBuf buf) {

        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
}

package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Utilities;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
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

public class OverlaysPacket {
    private static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath("fogoverrides", "overlays");
    private static final CustomPacketPayload.Type<PacketPayload> PACKET_TYPE = new CustomPacketPayload.Type<>(PACKET_ID);
    private static final StreamCodec<FriendlyByteBuf, PacketPayload> PACKET_CODEC = CustomPacketPayload.codec(PacketPayload::write, PacketPayload::new);

    public static Packet<ClientGamePacketListener> create(boolean waterOverlay, boolean fireOverlay, int fireOffset, int firePotionOffset) {
        return (Packet<ClientGamePacketListener>) NetworkManager.toPacket(NetworkManager.s2c(), new PacketPayload(waterOverlay, fireOverlay, fireOffset, firePotionOffset), null);
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
                boolean waterOverlay = payload.waterOverlay();
                boolean fireOverlay = payload.fireOverlay();
                int fireOffset = payload.fireOffset();
                int firePotOffset = payload.firePotionOffset();
                CurrentDataStorage.INSTANCE.updateOverlays(waterOverlay, fireOverlay, fireOffset, firePotOffset);
            });
        }
    }

    public record PacketPayload(boolean waterOverlay, boolean fireOverlay, int fireOffset, int firePotionOffset) implements CustomPacketPayload {
        public PacketPayload(FriendlyByteBuf buf) {
            this(buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeBoolean(waterOverlay);
            buf.writeBoolean(fireOverlay);
            buf.writeInt(fireOffset);
            buf.writeInt(firePotionOffset);
        }

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return PACKET_TYPE;
        }
    }
}

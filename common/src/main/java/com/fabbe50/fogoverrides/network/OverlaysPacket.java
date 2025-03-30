package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public class OverlaysPacket implements IDataPacket<ModConfig, OverlaysPacket.OverlaysPayload> {
    @Override
    public String getPacketID() {
        return "overlays";
    }

    @Override
    public void receiveClient(OverlaysPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received overlay settings from server: " + payload);
            boolean waterOverlay = payload.waterOverlay();
            boolean fireOverlay = payload.fireOverlay();
            int fireOffset = payload.fireOffset();
            int firePotOffset = payload.firePotionOffset();
            CurrentDataStorage.INSTANCE.updateOverlays(waterOverlay, fireOverlay, fireOffset, firePotOffset);
        });
    }

    @Override
    public void receiveServer(OverlaysPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer().hasPermissions(4)) {
                Log.info("Received overlay settings from admin client: " + payload);
                ModConfig.INSTANCE.renderWaterOverlay = payload.waterOverlay();
                ModConfig.INSTANCE.renderFireOverlay = payload.fireOverlay();
                ModConfig.INSTANCE.fireOverlayOffset = payload.fireOffset();
                ModConfig.INSTANCE.firePotionOverlayOffset = payload.firePotionOffset();
            }
        });
    }

    @Override
    public OverlaysPayload getPayload(FriendlyByteBuf buf) {
        return new OverlaysPayload(buf);
    }

    @Override
    public OverlaysPayload getDefaultPayload() {
        return new OverlaysPayload(true, true, 0, -25);
    }

    public record OverlaysPayload(boolean waterOverlay, boolean fireOverlay, int fireOffset, int firePotionOffset) implements IDataPayload<ModConfig, OverlaysPayload> {
        public OverlaysPayload(FriendlyByteBuf buf) {
            this(buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt());
        }

        @Override
        public OverlaysPayload read(FriendlyByteBuf buf) {
            return new OverlaysPayload(buf);
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, ModConfig data) {
            buf.writeBoolean(data.renderWaterOverlay);
            buf.writeBoolean(data.renderFireOverlay);
            buf.writeInt(data.fireOverlayOffset);
            buf.writeInt(data.firePotionOverlayOffset);
            return buf;
        }
    }
}

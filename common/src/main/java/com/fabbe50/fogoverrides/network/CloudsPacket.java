package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public class CloudsPacket implements IDataPacket<ModConfig, CloudsPacket.CloudPacketPayload> {
    @Override
    public String getPacketID() {
        return "clouds";
    }

    @Override
    public void receiveClient(CloudPacketPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received cloud settings from server: " + payload);
            CurrentDataStorage.INSTANCE.updateCloudHeight(payload.height());
        });
    }

    @Override
    public void receiveServer(CloudPacketPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer().hasPermissions(4)) {
                Log.info("Received cloud settings from admin client: " + payload);
                ModConfig.INSTANCE.cloudHeight = payload.height();
            }
        });
    }

    @Override
    public CloudPacketPayload getPayload(FriendlyByteBuf buf) {
        return new CloudPacketPayload(buf);
    }

    @Override
    public CloudPacketPayload getDefaultPayload() {
        return new CloudPacketPayload(192);
    }

    public record CloudPacketPayload(int height) implements IDataPayload<ModConfig, CloudPacketPayload> {
        private CloudPacketPayload(FriendlyByteBuf buf) {
            this(
                    buf.readInt()
            );
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, ModConfig config) {
            buf.writeInt(config.cloudHeight);
            return buf;
        }

        @Override
        public CloudPacketPayload read(FriendlyByteBuf buf) {
            return new CloudPacketPayload(buf);
        }
    }
}

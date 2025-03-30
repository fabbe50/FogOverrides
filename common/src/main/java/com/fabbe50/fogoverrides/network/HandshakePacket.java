package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ClientUtilities;
import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;


public class HandshakePacket implements IDataPacket<Boolean, HandshakePacket.HandshakePayload> {
    @Override
    public String getPacketID() {
        return "handshake";
    }

    @Override
    public void receiveClient(HandshakePayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received handshake packet from server with payload: " + payload);
            if (ClientUtilities.isIntegratedServer()) {
                CurrentDataStorage.INSTANCE.setIntegratedServer(true);
            }
            CurrentDataStorage.INSTANCE.setOnFogOverridesEnabledServer(payload.modEnabledServer());
        });
    }

    @Override
    public void receiveServer(HandshakePayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received handshake from client with payload: " + payload);
            if (payload.modEnabledServer()) {
                if (!NetworkHandler.modUsers.contains(context.getPlayer()))
                    NetworkHandler.modUsers.add(context.getPlayer());
                NetworkHandler.sendToPlayer(context.getPlayer(), new HandshakePacket(), true);
                NetworkHandler.sendSettingsToPlayer(context.getPlayer());
            }
        });
    }

    @Override
    public HandshakePayload getPayload(FriendlyByteBuf buf) {
        return new HandshakePayload(buf);
    }

    @Override
    public HandshakePayload getDefaultPayload() {
        return new HandshakePayload(true);
    }

    public record HandshakePayload(boolean modEnabledServer) implements IDataPayload<Boolean, HandshakePayload> {
        public HandshakePayload(FriendlyByteBuf buf) {
            this(buf.readBoolean());
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, Boolean data) {
            buf.writeBoolean(data);
            return buf;
        }

        @Override
        public HandshakePayload read(FriendlyByteBuf buf) {
            return new HandshakePayload(buf);
        }
    }
}

package com.fabbe50.fogoverrides.network.interfaces;

import com.fabbe50.fogoverrides.FogOverrides;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacket<T extends IPayload> {
    String getPacketID();

    default ResourceLocation getClientPacketID() {
        return FogOverrides.location("s2c_" + getPacketID());
    }

    default ResourceLocation getServerPacketID() {
        return FogOverrides.location("c2s_" + getPacketID());
    }

    default void registerClient() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), getClientPacketID(), this::receiveClient);
    }

    private void receiveClient(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        receiveClient(getPayload(buf), context);
    }

    void receiveClient(T payload, NetworkManager.PacketContext context);

    default void registerServer() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), getServerPacketID(), this::receiveServer);
    }

    private void receiveServer(FriendlyByteBuf buf, NetworkManager.PacketContext context) {
        receiveServer(getPayload(buf), context);
    }

    void receiveServer(T payload, NetworkManager.PacketContext context);

    T getDefaultPayload();

    T getPayload(FriendlyByteBuf buf);

    default T getEmptyPayload() {
        return getPayload(new FriendlyByteBuf(Unpooled.buffer()));
    }
}

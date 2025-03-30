package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.ClothScreen;
import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.network.interfaces.IPacket;
import com.fabbe50.fogoverrides.network.interfaces.IPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class OpenFogSettingsPacket implements IPacket<OpenFogSettingsPacket.OpenFogSettingsPayload> {

    @Override
    public String getPacketID() {
        return "fogsettings";
    }

    @Override
    public void receiveClient(OpenFogSettingsPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received open settings packet from server!");
            Minecraft.getInstance().setScreen(ClothScreen.getConfigScreen(null, CurrentDataStorage.INSTANCE.getServerSettings(), null));
        });
    }

    @Override
    public void receiveServer(OpenFogSettingsPayload payload, NetworkManager.PacketContext context) {
        Log.error("Open fog settings payload received server side. This should not happen.");
    }

    @Override
    public OpenFogSettingsPayload getPayload(FriendlyByteBuf buf) {
        return new OpenFogSettingsPayload(buf);
    }

    @Override
    public OpenFogSettingsPayload getDefaultPayload() {
        return new OpenFogSettingsPayload();
    }

    public record OpenFogSettingsPayload() implements IPayload {
        public OpenFogSettingsPayload(FriendlyByteBuf buf) {
            this();
        }
    }
}

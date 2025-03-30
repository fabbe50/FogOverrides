package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.data.CurrentDataStorage;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

public class LiquidsPacket implements IDataPacket<ModConfig, LiquidsPacket.LiquidPayload> {
    @Override
    public String getPacketID() {
        return "liquids";
    }

    @Override
    public void receiveClient(LiquidPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received liquid settings from server: " + payload);
            boolean waterFog = payload.waterFog();
            boolean lavaFog = payload.lavaFog();
            CurrentDataStorage.INSTANCE.updateLiquids(waterFog, lavaFog);
        });
    }

    @Override
    public void receiveServer(LiquidPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer().hasPermissions(4)) {
                Log.info("Received liquid settings from admin client: " + payload);
                ModConfig.INSTANCE.waterFogEnabled = payload.waterFog();
                ModConfig.INSTANCE.lavaFogEnabled = payload.lavaFog();
            }
        });
    }

    @Override
    public LiquidPayload getPayload(FriendlyByteBuf buf) {
        return new LiquidPayload(buf);
    }

    @Override
    public LiquidPayload getDefaultPayload() {
        return new LiquidPayload(true, true);
    }

    public record LiquidPayload(boolean waterFog, boolean lavaFog) implements IDataPayload<ModConfig, LiquidPayload> {
        public LiquidPayload(FriendlyByteBuf buf) {
            this(buf.readBoolean(), buf.readBoolean());
        }

        @Override
        public LiquidPayload read(FriendlyByteBuf buf) {
            return new LiquidPayload(buf);
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, ModConfig data) {
            buf.writeBoolean(data.waterFogEnabled);
            buf.writeBoolean(data.lavaFogEnabled);
            return buf;
        }
    }
}

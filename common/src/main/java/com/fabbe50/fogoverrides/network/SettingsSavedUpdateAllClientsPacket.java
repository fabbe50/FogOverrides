package com.fabbe50.fogoverrides.network;

import com.fabbe50.fogoverrides.Log;
import com.fabbe50.fogoverrides.ModConfig;
import com.fabbe50.fogoverrides.network.interfaces.IDataPacket;
import com.fabbe50.fogoverrides.network.interfaces.IDataPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class SettingsSavedUpdateAllClientsPacket implements IDataPacket<Integer, SettingsSavedUpdateAllClientsPacket.SettingsSavedUpdateAllClientsPayload> {
    @Override
    public String getPacketID() {
        return "update_settings";
    }

    @Override
    public void receiveClient(SettingsSavedUpdateAllClientsPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Log.info("Received save ping confirmation from server.");
            int i = payload.i();
            if (i == 1) {
                context.getPlayer().displayClientMessage(Component.translatable("text.fogoverrides.information.updated-players.single", payload.i()), false);
            } else {
                context.getPlayer().displayClientMessage(Component.translatable("text.fogoverrides.information.updated-players.multi", payload.i()), false);
            }
        });
    }

    @Override
    public void receiveServer(SettingsSavedUpdateAllClientsPayload payload, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer().hasPermissions(4)) {
                Log.info("Received save ping from admin client.");
                try {
                    ModConfig.save(ModConfig.getConfigFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ModConfig.load(ModConfig.getConfigFile());

                int updatedPlayers = NetworkHandler.sendSettingsToAllPlayers();
                if (updatedPlayers > 0) {
                    NetworkHandler.sendToPlayer(context.getPlayer(), new SettingsSavedUpdateAllClientsPacket(), updatedPlayers);
                }
            }
        });
    }

    @Override
    public SettingsSavedUpdateAllClientsPayload getPayload(FriendlyByteBuf buf) {
        return new SettingsSavedUpdateAllClientsPayload(buf);
    }

    @Override
    public SettingsSavedUpdateAllClientsPayload getDefaultPayload() {
        return new SettingsSavedUpdateAllClientsPayload(0);
    }

    public record SettingsSavedUpdateAllClientsPayload(int i) implements IDataPayload<Integer, SettingsSavedUpdateAllClientsPayload> {
        public SettingsSavedUpdateAllClientsPayload(FriendlyByteBuf buf) {
            this(buf.readInt());
        }

        @Override
        public SettingsSavedUpdateAllClientsPayload read(FriendlyByteBuf buf) {
            return new SettingsSavedUpdateAllClientsPayload(buf);
        }

        @Override
        public FriendlyByteBuf write(FriendlyByteBuf buf, Integer data) {
            buf.writeInt(data);
            return buf;
        }
    }
}

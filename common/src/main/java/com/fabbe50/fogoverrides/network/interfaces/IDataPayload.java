package com.fabbe50.fogoverrides.network.interfaces;

import net.minecraft.network.FriendlyByteBuf;

public interface IDataPayload<R, T> extends IPayload {
    T read(FriendlyByteBuf buf);

    FriendlyByteBuf write(FriendlyByteBuf buf, R data);
}

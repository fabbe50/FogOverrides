package com.fabbe50.fogoverrides.network.interfaces;

public interface IDataPacket<R, T extends IDataPayload<R, T>> extends IPacket<T> {

}

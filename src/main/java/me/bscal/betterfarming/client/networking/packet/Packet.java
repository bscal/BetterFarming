package me.bscal.betterfarming.client.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface Packet
{
	Identifier GetId();
	void Write(PacketByteBuf buf);
	void Apply();
}

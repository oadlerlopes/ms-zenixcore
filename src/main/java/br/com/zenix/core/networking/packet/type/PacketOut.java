package br.com.zenix.core.networking.packet.type;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.Packet;
import br.com.zenix.core.networking.packet.type.PacketType.PacketTypeOut;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class PacketOut extends Packet {

	public PacketOut(PacketTypeOut packetType) {
		super(packetType);
	}

	@Override
	public PacketTypeOut getPacketType() {
		return (PacketTypeOut) this.packetType;
	}

	public abstract void writePacketData(ByteArrayDataOutput output);
}

package br.com.zenix.core.networking.packet.type;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.Packet;
import br.com.zenix.core.networking.packet.type.PacketType.PacketTypeIn;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class PacketIn extends Packet {

	public PacketIn(PacketTypeIn packetType) {
		super(packetType);
	}

	@Override
	public PacketTypeIn getPacketType() {
		return (PacketTypeIn) this.packetType;
	}

	public abstract void readPacketData(ByteArrayDataInput input);

	public abstract void processPacketData(PacketProcessor processor);
}

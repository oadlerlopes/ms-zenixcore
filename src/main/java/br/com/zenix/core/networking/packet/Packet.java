package br.com.zenix.core.networking.packet;

import br.com.zenix.core.networking.packet.type.PacketType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class Packet {

	protected final PacketType packetType;

	protected Packet(PacketType packetType) {
		this.packetType = packetType;
	}

	public abstract PacketType getPacketType();
}

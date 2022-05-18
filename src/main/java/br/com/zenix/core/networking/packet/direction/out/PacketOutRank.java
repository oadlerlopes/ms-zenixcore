package br.com.zenix.core.networking.packet.direction.out;

import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketOutRank extends PacketOut {

	private final UUID targetUniqueId;
	private final Rank newRank;
	private final int expiry;

	public PacketOutRank(UUID targetUniqueId, Rank newGroup, int expiry) {
		super(PacketType.Out.RANK);

		this.targetUniqueId = targetUniqueId;
		this.newRank = newGroup;
		this.expiry = expiry;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeLong(targetUniqueId.getMostSignificantBits());
		output.writeLong(targetUniqueId.getLeastSignificantBits());
		output.writeByte(newRank.getId());
		output.writeInt(expiry);
	}
}

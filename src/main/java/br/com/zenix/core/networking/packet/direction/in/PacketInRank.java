package br.com.zenix.core.networking.packet.direction.in;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketInRank extends PacketIn {

	private UUID targetUniqueId;
	private Rank newRank;
	private int duration;

	public PacketInRank() {
		super(PacketType.In.RANK);
	}

	public UUID getTargetUniqueId() {
		return targetUniqueId;
	}

	public Rank getNewRank() {
		return newRank;
	}

	public int getDuration() {
		return duration;
	}

	public int getExpiry() {
		return duration == -1 ? -1 : Utils.unixTimestamp() + duration;
	}

	@Override
	public void readPacketData(ByteArrayDataInput input) {
		long most = input.readLong();
		long least = input.readLong();

		this.targetUniqueId = new UUID(most, least);

		int newGroupId = input.readByte();
		this.newRank = Core.getCoreManager().getPermissionManager().getRank(newGroupId);
		if (newRank == null) {
			throw new IllegalArgumentException("Invalid group id: " + newGroupId);
		}
		this.duration = input.readInt();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processGroup(this);
	}
}

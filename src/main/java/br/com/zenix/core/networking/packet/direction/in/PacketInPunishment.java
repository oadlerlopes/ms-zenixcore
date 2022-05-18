package br.com.zenix.core.networking.packet.direction.in;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketInPunishment extends PacketIn {

	private PunishType punishType;
	private int punishId;
	private UUID punished;
	private int staffId;
	private long start;
	private long expiry;
	private String reason;
	private String ip;

	public PacketInPunishment() {
		super(PacketType.In.PUNISHMENT);
	}

	public PunishType getPunishType() {
		return punishType;
	}

	public int getPunishId() {
		return punishId;
	}

	public UUID getPunishedUniqueId() {
		return punished;
	}

	public int getPunisherId() {
		return staffId;
	}

	public long getStart() {
		return start;
	}

	public long getExpiry() {
		return expiry;
	}

	public String getReason() {
		return reason;
	}
	
	public String getIp() {
		return ip;
	}

	@Override
	public void readPacketData(ByteArrayDataInput input) {
		int punishTypeId = input.readByte();
		this.punishType = PunishType.getType(punishTypeId);
		if (punishType == null) {
			throw new IllegalArgumentException("Invalid punishment type: " + punishTypeId);
		}

		this.punishId = input.readInt();

		long most = input.readLong();
		long least = input.readLong();

		this.punished = new UUID(most, least);
		this.staffId = input.readInt();
		this.start = input.readLong();
		this.expiry = input.readLong();
		this.reason = input.readUTF();
		this.ip = input.readUTF();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processPunishment(this);
	}
}

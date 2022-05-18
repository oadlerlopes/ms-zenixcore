package br.com.zenix.core.networking.packet.direction.out;

import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PacketOutPunishment extends PacketOut {

	private final PunishRecord punishRecord;
	private final UUID punishedUniqueId;
	private final String ip;

	public PacketOutPunishment(PunishRecord punishRecord, UUID punishedUniqueId, String ip) {
		super(PacketType.Out.PUNISHMENT);

		this.punishRecord = punishRecord;
		this.punishedUniqueId = punishedUniqueId;
		this.ip = ip;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeByte(punishRecord.getType().getId());
		output.writeInt(punishRecord.getId());
		output.writeLong(punishedUniqueId.getMostSignificantBits());
		output.writeLong(punishedUniqueId.getLeastSignificantBits());
		output.writeInt(punishRecord.getStaff());
		output.writeLong(punishRecord.getStart());
		output.writeLong(punishRecord.getExpire());
		output.writeUTF(punishRecord.getMotive());
		output.writeUTF(ip);
	}
}

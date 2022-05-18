package br.com.zenix.core.networking.packet.direction.out;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;

public final class PacketOutSetPermission extends PacketOut {

	private final Rank rank;
	private final String permission;
	private final boolean value;

	public PacketOutSetPermission(Rank rank, String permission, boolean value) {
		super(PacketType.Out.PERMISSION_GROUP);

		this.rank = rank;
		this.permission = permission;
		this.value = value;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeByte(rank.getId());
		output.writeUTF(permission);
		output.writeBoolean(value);
	}
}

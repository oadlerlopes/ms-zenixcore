package br.com.zenix.core.networking.packet.direction.out;

import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;

public final class PacketOutPlayerPermission extends PacketOut {

	private final UUID uniqueId;
	private final String permission;
	private final boolean value;

	public PacketOutPlayerPermission(UUID uniqueId, String permission, boolean value) {
		super(PacketType.Out.PERMISSION_GROUP);

		this.uniqueId = uniqueId;
		this.permission = permission;
		this.value = value;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeLong(uniqueId.getMostSignificantBits());
		output.writeLong(uniqueId.getLeastSignificantBits());
		output.writeUTF(permission);
		output.writeBoolean(value);
	}
}

package br.com.zenix.core.networking.packet.direction.in;

import java.util.UUID;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;

public final class PacketInPlayerPermission extends PacketIn {

	private UUID uniqueId;
	private String permission;
	private boolean value;

	public PacketInPlayerPermission() {
		super(PacketType.In.PERMISSION_GROUP);
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public String getPermission() {
		return permission;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void readPacketData(ByteArrayDataInput input) {
		long most = input.readLong();
		long least = input.readLong();

		this.uniqueId = new UUID(most, least);
		this.permission = input.readUTF();
		this.value = input.readBoolean();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processPlayerPermission(this);
	}
}

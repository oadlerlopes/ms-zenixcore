package br.com.zenix.core.networking.packet.direction.in;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;

public final class PacketInSetPermission extends PacketIn {

	private Rank rank;
	private String permission;
	private boolean value;

	public PacketInSetPermission() {
		super(PacketType.In.PERMISSION_GROUP);
	}

	public Rank getRank() {
		return rank;
	}

	public String getPermission() {
		return permission;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void readPacketData(ByteArrayDataInput input) {
		int groupId = input.readByte();
		this.rank = Core.getCoreManager().getPermissionManager().getRank(groupId);
		if (rank == null) {
			throw new IllegalArgumentException("Invalid group id: " + groupId);
		}
		this.permission = input.readUTF();
		this.value = input.readBoolean();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processRankPermission(this);
	}
}

package br.com.zenix.core.networking.packet.direction.in;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;

public final class PacketInRankUpdate extends PacketIn {

	private Rank rank;
	private Tag tag;

	private boolean value;

	public PacketInRankUpdate() {
		super(PacketType.In.RANK_UPDATE);
	}

	public Rank getRank() {
		return rank;
	}

	public Tag getTag() {
		return tag;
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

		int tagId = input.readInt();
		this.tag = Core.getCoreManager().getTagManager().getTag(tagId);
		if (tag == null) {
			throw new IllegalArgumentException("Invalid tag id: " + groupId);
		}

		this.value = input.readBoolean();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processRankUpdate(this);
	}
}

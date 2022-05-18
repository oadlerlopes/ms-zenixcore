package br.com.zenix.core.networking.packet.direction.out;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;

public final class PacketOutRankUpdate extends PacketOut {

	private final Rank rank;
	private final Tag tag;
	private final boolean value;

	public PacketOutRankUpdate(Rank rank, Tag tag, boolean value) {
		super(PacketType.Out.RANK_UPDATE);

		this.rank = rank;
		this.tag = tag;
		this.value = value;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeByte(rank.getId());
		output.writeInt(tag.getId());
		output.writeBoolean(value);
	}
}

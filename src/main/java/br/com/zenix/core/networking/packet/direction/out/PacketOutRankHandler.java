package br.com.zenix.core.networking.packet.direction.out;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;

public final class PacketOutRankHandler extends PacketOut {

	private final Rank rank;
	private final boolean value;

	public PacketOutRankHandler(Rank rank, boolean value) {
		super(PacketType.Out.RANK_HANDLER);

		this.rank = rank;
		this.value = value;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeByte(rank.getId());
		output.writeUTF(rank.getName());
		output.writeBoolean(value);
	}
}

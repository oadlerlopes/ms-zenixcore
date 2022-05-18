package br.com.zenix.core.networking.packet.direction.out;

import com.google.common.io.ByteArrayDataOutput;

import br.com.zenix.core.networking.packet.type.PacketOut;
import br.com.zenix.core.networking.packet.type.PacketType;

public final class PacketOutClanChat extends PacketOut {

	private final String message;
	private final String clanName;

	public PacketOutClanChat(String message, String clanName) {
		super(PacketType.Out.CLAN_CHAT);

		this.message = message;
		this.clanName = clanName;
	}

	@Override
	public void writePacketData(ByteArrayDataOutput output) {
		output.writeUTF(message);
		output.writeUTF(clanName);
	}
}

package br.com.zenix.core.networking.packet.direction.in;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;

public final class PacketInClanChat extends PacketIn {

	private String message;
	private String clanName;

	public PacketInClanChat() {
		super(PacketType.In.CLAN_CHAT);
	}

	public String getMessage() {
		return message;
	}
	
	public String getClanName() {
		return clanName;
	}

	@Override
	public void readPacketData(ByteArrayDataInput input) {
		this.message = input.readUTF();
		this.clanName = input.readUTF();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processClanChat(this);
	}
}

package br.com.zenix.core.networking.packet.direction.in;

import com.google.common.io.ByteArrayDataInput;

import br.com.zenix.core.networking.PacketProcessor;
import br.com.zenix.core.networking.packet.type.PacketIn;
import br.com.zenix.core.networking.packet.type.PacketType;

public final class PacketInRankHandler extends PacketIn {

	private int group;
	private String name;
	private boolean value;

	public PacketInRankHandler() {
		super(PacketType.In.RANK_HANDLER);
	}

	public int getRank() {
		return group;
	}

	public String getName() {
		return name;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public void readPacketData(ByteArrayDataInput input) {
		this.group = input.readByte();
		this.name = input.readUTF();
		this.value = input.readBoolean();
	}

	@Override
	public void processPacketData(PacketProcessor processor) {
		processor.processRankHandler(this);
	}
}

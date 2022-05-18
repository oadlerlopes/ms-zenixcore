package br.com.zenix.core.spigot.anticheat.event;

import br.com.zenix.core.spigot.player.events.CustomEvent;

public class UpdateEvent extends CustomEvent {

	private UpdateType type;

	public UpdateEvent(UpdateType Type) {
		this.type = Type;
	}

	public UpdateType getType() {
		return type;
	}

}
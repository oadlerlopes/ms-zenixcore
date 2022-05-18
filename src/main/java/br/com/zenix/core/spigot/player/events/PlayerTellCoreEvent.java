package br.com.zenix.core.spigot.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PlayerTellCoreEvent extends CustomEvent implements Cancellable {

	private final Player player, target;
	private boolean cancel;
	private String message;

	public PlayerTellCoreEvent(Player player, Player target, String message) {
		this.message = message;
		this.player = player;
		this.target = target;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

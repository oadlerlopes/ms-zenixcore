package br.com.zenix.core.spigot.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PlayerSkinFakeChange extends CustomEvent implements Cancellable {

	private final Player player;
	private boolean cancel;

	public PlayerSkinFakeChange(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}

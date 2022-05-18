package br.com.zenix.core.spigot.player.events;

import org.bukkit.entity.Player;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class PlayerAdminEvent extends CustomEvent {

	private final Player player;
	private final boolean join;

	public PlayerAdminEvent(Player player, boolean join) {
		this.player = player;
		this.join = join;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isJoin() {
		return join;
	}
}

package br.com.zenix.core.spigot.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PlayerInventoryOpenEvent extends CustomEvent implements Cancellable {

	private final Player player;
	private final Inventory inventory;

	private boolean cancelled = false;

	public PlayerInventoryOpenEvent(Player player, Inventory inventory) {
		this.player = player;
		this.inventory = inventory;
	}

	public Player getPlayer() {
		return player;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}

package br.com.zenix.core.spigot.player.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class CustomEvent extends Event {

	private static HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}

package br.com.zenix.core.spigot.player.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class PlayerChatCoreEvent extends CustomEvent implements Cancellable {

	private final Player player;
	private boolean cancel;
	private final Set<Player> recipients;
	private String message, format;

	public PlayerChatCoreEvent(Player clicked, String message, String format, Set<Player> players) {
		this.player = clicked;
		this.message = message;
		this.format = format;
		this.recipients = players;
	}

	public Set<Player> getRecipients() {
		return recipients;
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

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}
}

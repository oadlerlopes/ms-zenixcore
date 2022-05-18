package br.com.zenix.core.proxy.server;

import net.md_5.bungee.api.ChatColor;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public enum ServerStatus {

	ONLINE(0, ChatColor.GREEN),
	OFFLINE(1, ChatColor.RED),
	STARTING(2, ChatColor.YELLOW),
	PREGAME(10, ChatColor.AQUA),
	INVENCIBILITY(11, ChatColor.GOLD),
	GAME(12, ChatColor.BLUE),
	FULL(3, ChatColor.DARK_RED);

	private final int status;
	private final ChatColor color;

	private ServerStatus(int status, ChatColor color) {
		this.status = status;
		this.color = color;
	}

	public int getStatus() {
		return status;
	}

	public ChatColor getColor() {
		return color;
	}

	public static ServerStatus getServerStatus(int status) {
		ServerStatus type = ServerStatus.OFFLINE;
		for (ServerStatus types : values())
			if (types.getStatus() == status)
				type = types;
		return type;
	}

	public static ServerStatus getServerStatus(String status) {
		return getServerStatus(Integer.valueOf(status));
	}
}

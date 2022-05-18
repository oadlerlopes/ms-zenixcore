package br.com.zenix.core.spigot.server.type;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum ServerType {

	LOBBY(0, "Lobby", "LOBBY", 8192),
	HG(1, "HungerGames", "HG", 1024),
	PVP(2, "PvP", "PVP", 2560),
	SS(3, "Screenshare", "SS", 4074),
	NONE(4, "Nenhum", "NRE", 8704),
	PRACTICE(5, "Practice", "PRACTICE", 9216),
	GLADIATOR(6, "Gladiator", "GLADIATOR", 10240),
    SKYWARS(8, "SkyWars", "SW", 13280),
	ALL(-1, "Todos", "ALL", 0);

	private final int id, port;
	private final String name, simple;

	ServerType(int id, String name, String simple, int port) {
		this.id = id;
		this.name = name;
		this.simple = simple;
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public String getSimple() {
		return simple;
	}

	public static ServerType getServerType(String name) {
		ServerType type = ServerType.NONE;
		for (ServerType types : values())
			if (types.getName().equalsIgnoreCase(name))
				type = types;
		return type;
	}

	public static ServerType getServerType(int id) {
		ServerType type = ServerType.NONE;
		for (ServerType types : values())
			if (types.getId() == id)
				type = types;
		return type;
	}
}

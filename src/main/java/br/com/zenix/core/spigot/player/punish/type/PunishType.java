package br.com.zenix.core.spigot.player.punish.type;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum PunishType {

	BAN(0),
	TEMPBAN(1),
	WARN(2),
	TEMPWARN(3),
	MUTE(4),
	TEMPMUTE(5),
	KICK(6);

	private final int id;

	PunishType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static PunishType getType(int id) {
		PunishType type = null;
		for (PunishType types : values())
			if (types.getId() == id)
				type = types;
		return type;
	}

	public static PunishType getType(String name) {
		PunishType type = null;
		for (PunishType types : values())
			if (types.name().toLowerCase().equalsIgnoreCase(name))
				type = types;
		return type;
	}
}

package br.com.zenix.core.spigot.player.clan.groups;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public enum ClanHierarchy {

	OWNER(1, "OWNER"),
	ADMIN(2, "ADMIN"),
	MEMBER(3, "MEMBER");

	private String name;
	private int id;

	private ClanHierarchy(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}

}

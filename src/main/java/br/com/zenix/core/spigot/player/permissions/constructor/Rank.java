package br.com.zenix.core.spigot.player.permissions.constructor;

import java.util.HashMap;

import br.com.zenix.core.spigot.player.tag.constructor.Tag;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class Rank {

	private final HashMap<String, Boolean> permissions;

	private final String name;
	private final int id;
	private Tag tag;
	private long time;
	private boolean defaultRank, vip;

	public Rank(String name, int id, Tag tag, long time, boolean defaultRank) {
		this.name = name;
		this.id = id;
		this.tag = tag;
		this.time = time;
		this.defaultRank = defaultRank;
		this.permissions = new HashMap<>();
	}

	public void addPermission(String permission, Boolean active) {
		permissions.put(permission, active);
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Boolean> getPermissions() {
		return permissions;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public int getId() {
		return id;
	}

	public Tag getTag() {
		return tag;
	}

	public boolean isDefaultRank() {
		return defaultRank;
	}

	public boolean isVip() {
		return vip;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public void setDefaultRank(boolean defaultRank) {
		this.defaultRank = defaultRank;
	}

}

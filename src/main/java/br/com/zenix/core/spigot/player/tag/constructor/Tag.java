package br.com.zenix.core.spigot.player.tag.constructor;

import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.player.account.Account;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class Tag {

	private final int id;
	private final String name;
	private String prefix, color;
	private int order;

	public Tag(int id, String name, String prefix, String color, int order) {
		this.id = id;
		this.name = name;
		this.prefix = prefix;
		this.color = color;
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getColor() {
		return color;
	}

	public int getId() {
		return id;
	}

	public int getOrder() {
		return order;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean hasTag(Player player) {
		if (name.equals("Membro") || name.startsWith("Membro")) {
			return true;
		}

		CoreManager coreManager = Core.getCoreManager();

		Account account = coreManager.getAccountManager().getAccount(player);

		if (account.getRank() != null) {
			if (name.equals("Diretor") || name.startsWith("Diretor")) {
				if (account.getRank().getId() > 4) {
					return false;
				}
			}
			if (name.equals("Developer") || name.startsWith("Developer")) {
				if (account.getRank().getId() > 3) {
					return false;
				}
			}
			if (name.equals("Dono") || name.startsWith("Dono")) {
				if (account.getRank().getId() > 2) {
					return false;
				}
			}
		}

		return player.getPlayer().hasPermission("commons.tag." + name)
				|| player.getPlayer().hasPermission("commons.tag." + name);
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
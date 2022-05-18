package br.com.zenix.core.spigot.player.clan.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class ClanAccountManager extends Management {

	private HashMap<UUID, ClanAccount> clanAccounts = new HashMap<UUID, ClanAccount>();

	public ClanAccountManager(CoreManager manager) {
		super(manager, "ClanAccount");
	}

	public boolean initialize() {
		return true;
	}

	public ClanAccount addClanAccount(ClanAccount clanAccount) {
		clanAccounts.put(clanAccount.getUniqueId(), clanAccount);
		return clanAccount;
	}

	public void removeAccount(UUID uniqueId) {
		clanAccounts.remove(uniqueId);
	}

	public ClanAccount getClanAccount(UUID uniqueId) {
		return clanAccounts.get(uniqueId);
	}

	public ClanAccount getClanAccount(Player player) {
		return clanAccounts.get(player.getUniqueId());
	}

	public HashMap<UUID, ClanAccount> getClanAccounts() {
		return clanAccounts;
	}

}

package br.com.zenix.core.proxy.player.account;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ProxyAccountManager extends SimpleHandler {

	private static final HashMap<UUID, ProxyAccount> accounts = new HashMap<>();

	public ProxyAccountManager(ProxyManager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public ProxyAccount craftAccount(ProxiedPlayer player) {
		ProxyAccount account = new ProxyAccount(player);
		accounts.put(player.getUniqueId(), account);
		return account;
	}

	public ProxyAccount getAccount(Player player) {
		return accounts.get(player.getUniqueId());
	}

	public ProxyAccount getAccount(UUID uuid) {
		return accounts.get(uuid);
	}

	public void unloadAccount(UUID uuid) {
		ProxyAccount account = getAccount(uuid);
		account.cleanUp();
		accounts.remove(uuid);
	}

	public HashMap<UUID, ProxyAccount> getAccounts() {
		return accounts;
	}

}

package br.com.zenix.core.spigot.player.account;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class AccountManager extends Management {

	private static final HashMap<UUID, Account> accounts = new HashMap<>(200);

	public AccountManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return true;
	}

	public void updateAccount(Player player) {
		Account account = getAccount(player);
		if (account != null) {
			account.update();
		}
	}

	public Account craftAccount(Player player) {
		Account account = new Account(player.getUniqueId());
		accounts.put(player.getUniqueId(), account);
		return account;
	}

	public Account craftAccount(UUID uuid) {
		Account account = new Account(uuid);
		accounts.put(uuid, account);
		return account;
	}

	public Account getAccount(Player player) {
		return accounts.get(player.getUniqueId());
	}

	public Account getAccount(String id) {
		return accounts.get(getCoreManager().getNameFetcher().getUUID(id));
	}

	public Account getAccount(int id) {
		return accounts.get(getCoreManager().getNameFetcher().getUUID(id + ""));
	}

	public Account getAccount(UUID uuid) {
		return accounts.get(uuid);
	}

	public void unloadAccount(UUID uuid) {
		Account account = getAccount(uuid);

		if (account != null)
			account.cleanUp();
		accounts.remove(uuid);
	}

	public HashMap<UUID, Account> getAccounts() {
		return accounts;
	}
	
	public static final class MacroStatus extends GamerStatusBase {

		private int clicks;
		private int alerts;

		public MacroStatus(Account gamer) {
			super(gamer);
		}

		public int getAlerts() {
			return alerts;
		}

		public int getClicks() {
			return clicks;
		}

		public void setAlerts(int alerts) {
			this.alerts = alerts;
		}

		public void setClicks(int clicks) {
			this.clicks = clicks;
		}

		public void resetClicks() {
			this.clicks = 0;
		}
	}

	public static abstract class GamerStatusBase {

		private final Account account;

		public GamerStatusBase(Account account) {
			this.account = account;
		}

		public int getId() {
			return account.getId();
		}

	}

	public static abstract class AccountStatusBase {
		private final Account account;
		private final Executor executor;
		private boolean loaded;

		public AccountStatusBase(Account account) {
			this.account = account;
			this.executor = account.getSaveAsyncExecutor();
			this.loaded = false;
		}

		public abstract boolean load(boolean newAccount);

		public abstract boolean update();

		public void setLoaded(boolean loaded) {
			this.loaded = loaded;
		}

		public boolean isLoaded() {
			return loaded;
		}

		public Account getAccount() {
			return account;
		}

		public int getId() {
			return account.getId();
		}

		public Executor getExecutor() {
			return executor;
		}
	}

}
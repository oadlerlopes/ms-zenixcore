package br.com.zenix.core.proxy.player.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.proxy.Proxy;
import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class ProxyAccount {

	private static final Executor loadAsyncExecutor = Executors
			.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Load Async Thread").build());

	private static final HashMap<String, Boolean> permissions = new HashMap<>();

	private ProxiedPlayer player;
	private UUID uniqueId;
	private Rank rank;
	private int groupId;
	private boolean silent;

	private int id;

	public ProxyAccount(ProxiedPlayer player) {
		this.player = player;
		this.uniqueId = player.getUniqueId();
		this.id = 1;
		this.silent = false;

		long start = System.currentTimeMillis();
		loadAsyncExecutor.execute(() -> {
			try {

				Connection mainConnection = getProxyManager().getMySQL().getConnection();
				PreparedStatement accountQuery = mainConnection.prepareStatement(CoreQueries.ACCOUNT_QUERY.toString());
				accountQuery.setString(1, uniqueId.toString());

				ResultSet accountData = accountQuery.executeQuery();
				if (accountData.next()) {

					this.id = accountData.getInt(1);
					this.groupId = accountData.getInt(4);

					accountData.close();
					accountQuery.close();

					PreparedStatement preparedStatement = mainConnection
							.prepareStatement(CoreQueries.ACCOUNT_PERMISSIONS_SELECT.toString());
					preparedStatement.setInt(1, id);

					ResultSet resultSet = preparedStatement.executeQuery();

					while (resultSet.next()) {
						long time = resultSet.getLong(6) * 1000;

						if (time > 10000) {
							if (!new Date().after(new Date(time)))
								continue;
						}

						String permission = resultSet.getString(2);
						boolean active = Utils.getBooleanByInteger(resultSet.getInt(5));
						getPermissions().put(permission, active);
					}

					resultSet.close();
					preparedStatement.close();
				} else {
					rank = getProxyManager().getGroupManager().getRank();
				}

				rank = getProxyManager().getGroupManager().getRank(groupId);
				if (rank != null) {
					br.com.zenix.core.proxy.manager.managements.PermissionManager.updatePermissions(this);
				}
				
				if (getProxyManager().isMaintenance()) {
					if (!player.hasPermission("server.whitelist.admin")) {
						player.disconnect(TextComponent
								.fromLegacyText("§c§lZENIX\n\n§cAtualmente os servidores estão indisponíveis para players comuns.\n§cOs servidores estão em manutenção no momento, aguarde e tente novamente!\n\n§cwww.zenix.cc"));
					}
				}

				getLogger().log("[" + (System.currentTimeMillis() - start) + "ms] The player " + player.getName()
						+ " have your account loaded with success!");
			} catch (Exception exception) {
				exception.printStackTrace();
				getLogger().error("[" + (System.currentTimeMillis() - start)
						+ "ms] Error when the plugin tried to load the data of player with the uuid: " + uniqueId
						+ ".", exception);
			}
		});
	}

	public void unload() {
		getProxyManager().getAccountManager().unloadAccount(uniqueId);
	}

	public void cleanUp() {
		player = null;
		permissions.clear();
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isSilent() {
		return silent;
	}

	public Rank getGroup() {
		return rank;
	}

	public ProxiedPlayer getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public int getId() {
		return id;
	}

	public HashMap<String, Boolean> getPermissions() {
		return permissions;
	}

	public Executor getLoadAsyncExecutor() {
		return loadAsyncExecutor;
	}

	public Logger getLogger() {
		return getProxyManager().getAccountManager().getLogger();
	}

	public CoreManager getCommonsManager() {
		return Core.getCoreManager();
	}

	public ProxyManager getProxyManager() {
		return Proxy.getProxyManager();
	}

}

package br.com.zenix.core.proxy.manager.managements;

import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import br.com.zenix.core.proxy.server.FullyServerStatus;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class RedirectManager extends SimpleHandler {

	public RedirectManager(ProxyManager manager) {
		super(manager);
	}

	public boolean initialize() {
		return true;
	}

	public ServerInfo getAvailibleServer(ProxiedPlayer player, String serverName) {
		ServerInfo lastData = null;
		for (Integer port : getProxyManager().getPingManager().getAllStatus().keySet()) {
			FullyServerStatus ping = getProxyManager().getPingManager().getServerStatus(port);
			if (ping == null || !ping.getName().toUpperCase().startsWith(serverName.toUpperCase()))
				continue;
			int players = ping.getPlayers();
			if (getProxyManager().getPingManager().getCanJoin(port) && (lastData == null
					|| (players < ping.getMaxPlayers() && players > lastData.getPlayers().size()))) {
				lastData = getProxyManager().getPlugin().getProxy().getServerInfo(ping.getName());
			}
		}
		return lastData;
	}

	public ServerInfo getMinimumAvailibleServer(ProxiedPlayer player, String serverName) {
		ServerInfo lastData = null;
		for (Integer port : getProxyManager().getPingManager().getAllStatus().keySet()) {
			FullyServerStatus ping = getProxyManager().getPingManager().getServerStatus(port);
			if (ping == null || !ping.getName().toUpperCase().startsWith(serverName.toUpperCase()))
				continue;
			int players = ping.getPlayers();
			if (getProxyManager().getPingManager().getCanJoin(port) && (lastData == null
					|| (players < ping.getMaxPlayers() && players <= lastData.getPlayers().size()))) {
				lastData = getProxyManager().getPlugin().getProxy().getServerInfo(ping.getName());
			}
		}
		return lastData;
	}

	public ServerInfo getHungerGamesServer(ProxiedPlayer player) {
		return getAvailibleServer(player, "HG");
	}

	public ServerInfo getPvPServer(ProxiedPlayer player) {
		return getAvailibleServer(player, "PVP");
	}

	public ServerInfo getPracticeServer(ProxiedPlayer player) {
		return getAvailibleServer(player, "PRACTICE");
	}

	public ServerInfo getGladiatorServer(ProxiedPlayer player) {
		return getAvailibleServer(player, "GLADIATOR");
	}

	public ServerInfo getOitcServer(ProxiedPlayer player) {
		return getAvailibleServer(player, "OITC");
	}

	public ServerInfo getSkyWars(ProxiedPlayer player) {
		return getAvailibleServer(player, "SW");
	}
	
	public ServerInfo getEventServer(ProxiedPlayer player) {
		return getAvailibleServer(player, "EVENTO");
	}

	public ServerInfo getLobbyServer(ProxiedPlayer player) {
		return getMinimumAvailibleServer(player, "LOBBY");
	}

	public void redirectPlayerToServer(ProxiedPlayer player, String server) {
		player.connect(getProxyManager().getPlugin().getProxy().getServerInfo(server));
	}

	public void redirectPlayerToRandomServer(ProxiedPlayer player, String server) {
		player.connect(getAvailibleServer(player, server));
	}

}

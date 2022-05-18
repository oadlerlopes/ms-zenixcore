package br.com.zenix.core.proxy.player.listeners;

import br.com.zenix.core.proxy.commands.base.ProxyListener;
import br.com.zenix.core.proxy.utilitaries.StringUtils;
import br.com.zenix.core.spigot.server.type.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class RedirectListener extends ProxyListener {

	@EventHandler
	public void serverConnect(ServerConnectEvent event) {
		if (event.getPlayer().getServer() != null) {
			return;
		}

		String hostname = event.getPlayer().getPendingConnection().getVirtualHost().getHostName();

		ProxiedPlayer player = event.getPlayer();
		ServerInfo lastData = null;

		if (hostname.equalsIgnoreCase("zenix.cc") || hostname.equalsIgnoreCase("wombocraft.com.br") || hostname.equalsIgnoreCase("gama.zenix.cc") || hostname.equalsIgnoreCase("dc-f9c3cef52130.zenix.cc")) {
			lastData = getProxyManager().getRedirectManager().getLobbyServer(event.getPlayer());
		} else if (hostname.equalsIgnoreCase("hg.zenix.cc")) {
			lastData = getProxyManager().getRedirectManager().getHungerGamesServer(event.getPlayer());
		} else if (hostname.equalsIgnoreCase("eventos.zenix.cc")) {
			lastData = getProxyManager().getRedirectManager().getEventServer(event.getPlayer());
		} else if (hostname.equalsIgnoreCase("pvp.zenix.cc")) {
			lastData = getProxyManager().getRedirectManager().getPvPServer(event.getPlayer());
		} else if (hostname.equalsIgnoreCase("gladiator.zenix.cc")) {
			lastData = getProxyManager().getRedirectManager().getGladiatorServer(event.getPlayer());
		} else if (hostname.equalsIgnoreCase("practice.zenix.cc") || hostname.equalsIgnoreCase("pot.zenix.cc")) {
			lastData = getProxyManager().getRedirectManager().getPracticeServer(event.getPlayer());
		} else if (hostname.toLowerCase().endsWith("hg.zenix.cc")) {
			lastData = ProxyServer.getInstance().getServerInfo("HG-" + hostname.toLowerCase().replace(".hg.zenix.cc", "").replace("a", ""));
		} else if (hostname.toLowerCase().endsWith("event.zenix.cc")) {
			lastData = ProxyServer.getInstance().getServerInfo("EVENTO-" + hostname.toLowerCase().replace(".event.zenix.cc", "").replace("a", ""));
		} else if (hostname.toLowerCase().endsWith("pvp.zenix.cc")) {
			lastData = ProxyServer.getInstance().getServerInfo("PVP-" + hostname.toLowerCase().replace(".simulator.pvp.zenix.cc", "").replace(".fulliron.pvp.zenix.cc", "").replace("a", ""));
		} else if (hostname.equalsIgnoreCase("localhost")) {
			return;
		}

		if (lastData == null) {
			player.disconnect(TextComponent.fromLegacyText("§cO servidor não está disponível!"));
			event.setCancelled(true);
		} else {
			event.setTarget(lastData);
		}
	}

	@EventHandler
	public void motdManager(ProxyPingEvent event) {
		ServerPing conn = event.getResponse();
		if (event.getConnection() == null || event.getConnection().getVirtualHost() == null || event.getConnection().getVirtualHost().getHostName() == null) {
			conn.setDescription("§cEste IP não é válido! Conecte-se pelo IP direto: zenix.cc");
			conn.setVersion(new Protocol("Inexistente", 2));
			return;
		}

		if (event.getResponse() == null || event.getResponse().getPlayers() == null || event.getResponse().getVersion() == null || event.getResponse().getDescription().equals("&cServidor reiniciando...")) {
			conn.setDescription("§cEste IP não é válido! Conecte-se pelo IP direto: zenix.cc");
			conn.setVersion(new Protocol("Inexistente", 2));
			return;
		}

		event.getResponse().getModinfo().setType("VANILLA");

		String host = event.getConnection().getVirtualHost().getHostName();

		conn.setPlayers(new Players(1056, event.getResponse().getPlayers().getOnline(), getProxyManager().getPingManager().getPlayerInfo()));
		conn.getPlayers().setOnline(event.getResponse().getPlayers().getOnline());
		conn.setVersion(conn.getVersion());

		getProxyManager().getPingManager().setLastServerPing(conn);

		boolean found = false;

		for (ServerType type : ServerType.values()) {
			if (found)
				break;
			if (host.equalsIgnoreCase(type.name().toLowerCase() + ".zenix.cc") || host.equalsIgnoreCase(type.name().toLowerCase() + ".gama.zenix.cc")
					|| host.equalsIgnoreCase(type.name().toLowerCase() + ".dc-f9c3cef52130.zenix.cc")) {

				conn.setDescription(StringUtils.makeCenteredMotd("§3§l§m---§b§l§m---§f§l§m---§f§l» Zenix §f§l«§m---§b§l§m---§3§l§m---") + "\n§r" + StringUtils.makeCenteredMotd(getProxyManager().getMotdManager().getMotdSet()));
				found = true;
			}
		}

		if (!found && (host.equalsIgnoreCase("zenix.cc")) | (host.equalsIgnoreCase("gama.zenix.cc")) | (host.equalsIgnoreCase("dc-f9c3cef52130.zenix.cc")) | (host.equalsIgnoreCase("wombocraft.com.br"))) {
			conn.setDescription(StringUtils.makeCenteredMotd("§3§l§m---§b§l§m---§f§l§m---§f§l» Zenix §f§l«§m---§b§l§m---§3§l§m---") + "\n§r" + StringUtils.makeCenteredMotd(getProxyManager().getMotdManager().getMotdSet()));
			found = true;
		}

		if (getProxyManager().isMaintenance()) {
			conn.setVersion(new Protocol("Maintenance", 2));
		}

		event.setResponse(conn);

	}
}

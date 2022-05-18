package br.com.zenix.core.proxy.player.listeners;

import br.com.zenix.core.proxy.commands.base.ProxyListener;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class LoginListener extends ProxyListener {

	@EventHandler
	public void onLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		getProxyManager().getAccountManager().craftAccount(player);
	}

	@EventHandler
	public void onPluginMessageEvent(PluginMessageEvent e) {
		Connection p = e.getSender();
		if (("WDL|INIT".equalsIgnoreCase(e.getTag())) && ((e.getSender() instanceof ProxiedPlayer))) {
			p.disconnect(new TextComponent("§cVocê está usando mod's que não são permitidos no servidor."));
		}
		if (("PERMISSIONSREPL".equalsIgnoreCase(e.getTag()))
				&& (new String(e.getData()).contains("mod.worlddownloader"))) {
			p.disconnect(new TextComponent("§cVocê está usando mod's que não são permitidos no servidor."));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onKick(ServerKickEvent event) {
		boolean punish = event.getKickReason().toLowerCase().contains("banido");
		boolean kickado = event.getKickReason().toLowerCase().contains("motivo");
		boolean swsend = event.getKickReason().toLowerCase().contains("[sw-send]");
		boolean swsendlb = event.getKickReason().toLowerCase().contains("[sw-send-lobby]");
		boolean dead = event.getKickReason().toLowerCase().contains("[dead-transfer-player]");
		boolean dead2 = event.getKickReason().toLowerCase().contains("morreu");

		ServerInfo lobby = getProxyManager().getRedirectManager().getLobbyServer(event.getPlayer());
		ServerInfo hg = getProxyManager().getRedirectManager().getHungerGamesServer(event.getPlayer());
		ServerInfo oitc = getProxyManager().getRedirectManager().getOitcServer(event.getPlayer());
		ServerInfo sw = getProxyManager().getRedirectManager().getSkyWars(event.getPlayer());

		if (lobby == null || punish || kickado) {
			event.getPlayer().disconnect(event.getKickReasonComponent());
			return;
		}

		if (dead || event.getKickedFrom().getName().toUpperCase().startsWith("OITC-")) {
			if (oitc == null) {
				event.setCancelServer(lobby);
				event.getPlayer().connect(lobby);

				event.setCancelled(true);
				return;
			}

			event.setCancelServer(oitc);
			event.getPlayer().connect(oitc);
			event.setCancelled(true);
			return;
		}

		if (dead2 || event.getKickedFrom().getName().startsWith("HG-")
				|| event.getKickedFrom().getName().startsWith("EVENTO-")) {
			event.setCancelServer(hg);
			event.getPlayer().connect(hg);
			event.setCancelled(true);
			return;
		}

		if (swsend) {
			event.setCancelServer(sw);
			event.getPlayer().connect(sw);
			event.setCancelled(true);
			return;
		}

		if (swsendlb) {
			event.setCancelServer(lobby);
			event.getPlayer().connect(lobby);
			event.setCancelled(true);
			return;
		}

		ProxiedPlayer player = event.getPlayer();
		player.sendMessage(TextComponent.fromLegacyText(event.getKickReason().toString()));
		event.setCancelled(true);
		event.setCancelServer(lobby);
	}

}

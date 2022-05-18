package br.com.zenix.core.proxy.commands.staff;

import java.util.Map;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class ServerCommand extends ProxyCommand {

	public ServerCommand() {
		super("server", "connect", "join");
	}

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
		Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
		if (args.length == 0) {
			TextComponent serverList = new TextComponent(
					ProxyServer.getInstance().getTranslation("server_list", new Object[0]));
			serverList.setColor(ChatColor.BLUE);
			boolean first = true;
			proxiedPlayer.sendMessage("§aUse: §f/connect <servidor>");
		} else {
			ServerInfo server = (ServerInfo) servers.get(args[0]);
			if (server == null) {
				proxiedPlayer.sendMessage("§cServidor inexistente.");
				return;
			}
			if (server.getName().startsWith("SS-")) {
				return;
			}
			if (server.getName().toUpperCase().equals("SS-01")) {
				return;
			}
			if (server == null) {
				proxiedPlayer.sendMessage("§cServidor inexistente.");
			} else {
				proxiedPlayer.sendMessage("§aConectando..");
				proxiedPlayer.connect(server);
			}
		}
	}

}

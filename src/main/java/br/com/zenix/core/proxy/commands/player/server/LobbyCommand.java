package br.com.zenix.core.proxy.commands.player.server;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class LobbyCommand extends ProxyCommand {

	public LobbyCommand() {
		super("lobby", "hub");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

		ServerInfo lastData = getProxyManager().getRedirectManager().getLobbyServer(proxiedPlayer);

		if (lastData == null) {
			proxiedPlayer.sendMessage(TextComponent.fromLegacyText("§cNenhum servidor disponível!"));
			return;
		}
		
		proxiedPlayer.sendMessage("§aConectando...");

		proxiedPlayer.connect(lastData);
		return;
	}
}

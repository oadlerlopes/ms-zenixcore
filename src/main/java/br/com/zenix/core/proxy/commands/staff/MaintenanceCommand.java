package br.com.zenix.core.proxy.commands.staff;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class MaintenanceCommand extends ProxyCommand {

	public MaintenanceCommand() {
		super("maintenance");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (isPlayer(commandSender)) {
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
			if (!hasAdmin(getProxyManager().getAccountManager().getAccount(proxiedPlayer.getUniqueId()))) {
				sendPermissionMessage(commandSender);
				return;
			}
		}

		if (args.length != 0) {
			commandSender.sendMessage("§aUse: §f/maintenance");
		} else {
			getProxyManager().setMaintenance(!getProxyManager().isMaintenance());

			commandSender.sendMessage(
					"§aVocê §f" + (getProxyManager().isMaintenance() ? "ativou" : "desativou")
							+ "§a a manutenção dos servidores!");

			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
				if (!pp.hasPermission("server.whitelist.admin")) {
					pp.disconnect(TextComponent
							.fromLegacyText("§c§lZENIX\n\n§cAtualmente os servidores estão indisponíveis para players comuns.\n§cOs servidores estão em manutenção no momento, aguarde e tente novamente!\n\n§cwww.zenix.cc"));
				}
			}
		}
		return;
	}
}

package br.com.zenix.core.proxy.commands.staff.message;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class ListCommand extends ProxyCommand {

	public ListCommand() {
		super("list", "glist");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {

		if (isPlayer(commandSender)) {
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

			ProxyAccount account = getProxyManager().getAccountManager().getAccount(proxiedPlayer.getUniqueId());
			if (!hasAdmin(account)) {
				sendPermissionMessage(commandSender);
				return;
			}
		}

		commandSender.sendMessage("§7Estatísticas de jogadores presentes no servidor.");
		commandSender.sendMessage("");
		commandSender.sendMessage(" §ePvP:");
		commandSender.sendMessage("  §7In-game: §f" + "0/240");
		commandSender.sendMessage("");
		commandSender.sendMessage(" §eHungerGames:");
		commandSender.sendMessage("  §7In-game: §f" + "0/640");
		commandSender.sendMessage("");
		commandSender.sendMessage(" §eSkyWars:");
		commandSender.sendMessage("  §7In-game: §f" + "0/300");
		commandSender.sendMessage("");
		commandSender.sendMessage(" §eOne in the chamber:");
		commandSender.sendMessage("  §7In-game: §f" + "0/72");
		commandSender.sendMessage("");
		commandSender.sendMessage(" §eLobby:");
		commandSender.sendMessage("  §7In-game: §f" + "0/300");
		commandSender.sendMessage("");
		commandSender.sendMessage(" §ePractice:");
		commandSender.sendMessage("  §7In-game: §f" + "0/120");
		commandSender.sendMessage("");
	}

}

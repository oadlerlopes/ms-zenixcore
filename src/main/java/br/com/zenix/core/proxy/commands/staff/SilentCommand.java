package br.com.zenix.core.proxy.commands.staff;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class SilentCommand extends ProxyCommand {

	public SilentCommand() {
		super("silent");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!isPlayer(commandSender)) {
			sendExecutorMessage(commandSender);
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

		ProxyAccount account = getProxyManager().getAccountManager().getAccount(proxiedPlayer.getUniqueId());

		if (!hasAccount(account)) {
			sendPermissionMessage(commandSender);
			return;
		}

		account.setSilent(!account.isSilent());
		proxiedPlayer.sendMessage("§aVocê "
				+ (account.isSilent() ? "§fdesabilitou".toUpperCase() : "§fhabilitou".toUpperCase())
				+ "§a as mensagens.");
	}

}

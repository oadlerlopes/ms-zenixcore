package br.com.zenix.core.proxy.commands.staff;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import br.com.zenix.core.proxy.player.listeners.MessageListener;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class SCCommand extends ProxyCommand {

	public SCCommand() {
		super("sc", "staffchat", "staff");
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

		if (!MessageListener.isStaffchat(proxiedPlayer.getUniqueId())) {
			MessageListener.addStaffChat(proxiedPlayer.getUniqueId());
			proxiedPlayer.sendMessage("§6§l[STAFFCHAT] §f- Você §2§lENTROU §fno chat da Staff!");
			return;
		} else {
			MessageListener.removeStaffChat(proxiedPlayer.getUniqueId());
			proxiedPlayer.sendMessage("§6§l[STAFFCHAT] §f- Você §c§lSAIU§f do chat da Staff!");

		}
	}

}

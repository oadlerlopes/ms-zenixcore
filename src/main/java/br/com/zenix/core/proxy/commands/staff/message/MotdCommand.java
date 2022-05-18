package br.com.zenix.core.proxy.commands.staff.message;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class MotdCommand extends ProxyCommand {

	public MotdCommand() {
		super("motd");
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

		if (!hasAdmin(account)) {
			sendPermissionMessage(commandSender);
			return;
		}

		if (args.length == 0) {
			commandSender.sendMessage("§aUse: §f/motd <set> <prefix>");
			commandSender.sendMessage("§aUse: §f/motd <reset>");
			commandSender.sendMessage("§aUse: §f/motd <prefix> <prefix>");
		} else {
			if (args[0].equalsIgnoreCase("set")) {
				if (args.length <= 1) {
					commandSender.sendMessage("§aUse: §f/motd <set> <message>");
					return;
				} else {
					getProxyManager().getMotdManager().setMotdSet(getProxyManager().getMotdManager().getPrefix() + "" + getArgs(args, 1).replaceAll("&", "§") + getProxyManager().getMotdManager().getSuffix());
					commandSender.sendMessage("§aVocê alterou a MOTD para " + getProxyManager().getMotdManager().getPrefix() + "" + getArgs(args, 1).replaceAll("&", "§") + getProxyManager().getMotdManager().getSuffix());
				}
			} else if (args[0].equalsIgnoreCase("reset")){
				getProxyManager().getMotdManager().setMotdSet(getProxyManager().getMotdManager().getMotdBackup());
				commandSender.sendMessage("§aVocê resetou a MOTD e foi ativado o backup da mesma.");
			} else if (args[0].equalsIgnoreCase("prefix")) {
				if (args.length <= 1) {
					commandSender.sendMessage("§aUse: §f/motd <prefix> <prefix>");
					return;
				} else {
					getProxyManager().getMotdManager().setPrefix("" + getArgs(args, 1).replaceAll("&", "§"));
					commandSender.sendMessage("§aVocê alterou o PREFIX para " + getArgs(args, 1).replaceAll("&", "§"));
				}
			}
		}

	}

}

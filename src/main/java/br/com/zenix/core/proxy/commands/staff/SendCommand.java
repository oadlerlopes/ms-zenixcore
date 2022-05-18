package br.com.zenix.core.proxy.commands.staff;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class SendCommand extends ProxyCommand {

	public SendCommand() {
		super("send", "enviar");
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

		if (args.length != 2) {
			commandSender.sendMessage("§aUse: §f/send <server|player|all|atual> <alvo>");
			return;
		}
		
		ServerInfo target = ProxyServer.getInstance().getServerInfo(args[1]);
		
		if (target == null) {
			commandSender.sendMessage("§cServidor inexistente.");
			return;
		}
		
		if (args[0].equalsIgnoreCase("all")) {
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				summon(p, target, commandSender);
			}
		} else if (args[0].equalsIgnoreCase("atual")) {
			if (!(commandSender instanceof ProxiedPlayer)) {
				return;
			}
			ProxiedPlayer player = (ProxiedPlayer) commandSender;
			for (ProxiedPlayer p : player.getServer().getInfo().getPlayers()) {
				summon(p, target, commandSender);
			}
		} else {
			ServerInfo serverTarget = ProxyServer.getInstance().getServerInfo(args[0]);
			if (serverTarget != null) {
				for (ProxiedPlayer p : serverTarget.getPlayers()) {
					summon(p, target, commandSender);
				}
			} else {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
				if (player == null) {
					commandSender.sendMessage("§cPlayer offline.");
					return;
				}
				summon(player, target, commandSender);
			}
		}
		commandSender.sendMessage("§aJogador(es) enviado(s) para o servidor alvo.");
	}

	private void summon(ProxiedPlayer player, ServerInfo target, CommandSender commandSender) {
		if ((player.getServer() != null) && (!player.getServer().getInfo().equals(target))) {
			player.connect(target);
		}
	}

	public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
		if ((args.length > 2) || (args.length == 0)) {
			return ImmutableSet.of();
		}
		Set<String> matches = new HashSet<String>();
		String search = args[0].toLowerCase();
		if (args.length == 1) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.getName().toLowerCase().startsWith(search)) {
					matches.add(player.getName());
				}
			}
			if ("all".startsWith(search)) {
				matches.add("all");
			}
			if ("atual".startsWith(search)) {
				matches.add("atual");
			}
		} else {
			search = args[1].toLowerCase();
			for (String server : ProxyServer.getInstance().getServers().keySet()) {
				if (server.toLowerCase().startsWith(search)) {
					matches.add(server);
				}
			}
		}
		return matches;
	}

}

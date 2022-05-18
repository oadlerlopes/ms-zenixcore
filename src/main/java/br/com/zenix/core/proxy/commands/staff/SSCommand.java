package br.com.zenix.core.proxy.commands.staff;

import java.util.HashSet;
import java.util.Set;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.minecraft.util.com.google.common.collect.ImmutableSet;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class SSCommand extends ProxyCommand {

	public SSCommand() {
		super("ss", "screenshare");
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

		if (!hasModGC(account)) {
			sendPermissionMessage(commandSender);
			return;
		}

		ServerInfo target = ProxyServer.getInstance().getServerInfo("SS-01".toUpperCase());
		ServerInfo lobby = getProxyManager().getRedirectManager().getLobbyServer(proxiedPlayer);

		if (args.length == 0) {
			showHelp(commandSender);
		} else {
			ProxiedPlayer playerTarget = ProxyServer.getInstance().getPlayer(args[0]);
			ProxiedPlayer player = (ProxiedPlayer) commandSender;
			
			if (target == null) {
				commandSender.sendMessage(new TextComponent("§cO servidor está offline."));
				return;
			}
			
			if (playerTarget != null) {
				if (playerTarget.getServer().getInfo().getName().toUpperCase().startsWith("SS")) {
					playerTarget.sendMessage(new TextComponent("§eVocê foi liberado da screenshare."));
					player.sendMessage(new TextComponent("§eVocê liberou um player da screenshare."));
					player.connect(lobby);
					playerTarget.connect(lobby);

					for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
						if (pp.hasPermission("commons.cmd.bungeemod")) {
							pp.sendMessage(
									new TextComponent("§a§lSCREENSHARE §f" + playerTarget.getName() + "(" + playerTarget.getUUID().toString() + ") foi LIBERADO da SCREENSHARE, via requisição de " + player.getName()));
						}
					}
				} else {
					summon(player, playerTarget, target);
				}
			} else {
				commandSender.sendMessage(new TextComponent("§cO player está offline."));
			}
		}
	}

	public void showHelp(CommandSender commandSender) {
		commandSender.sendMessage(new TextComponent("§aUse: §f/ss <player>"));
	}

	@SuppressWarnings("deprecation")
	private void summon(ProxiedPlayer player, ProxiedPlayer puxado, ServerInfo target) {
		if ((player.getServer() != null) && (!player.getServer().getInfo().equals(target))) {
			puxado.connect(target);
			player.connect(target);

			for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
				if (pp.hasPermission("commons.cmd.bungeemod")) {
					pp.sendMessage(new TextComponent("§a§lSCREENSHARE §f" + puxado.getName() + "(" + puxado.getUUID().toString() + ") foi PUXADO para SCREENSHARE, via requisição de §a§l" + player.getName()));
				}
			}
		}
	}

	public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
		if ((args.length > 2) || (args.length == 0)) {
			return ImmutableSet.of();
		}
		Set<String> match = new HashSet<>();
		if (args.length == 1) {
			String search = args[0].toLowerCase();
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.getName().toLowerCase().startsWith(search)) {
					match.add(player.getName());
				}
			}
		}
		return match;
	}

}

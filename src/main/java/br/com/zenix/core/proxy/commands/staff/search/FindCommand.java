package br.com.zenix.core.proxy.commands.staff.search;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class FindCommand extends ProxyCommand {

	public FindCommand() {
		super("find", "encontrar");
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

		if (args.length != 1) {
			commandSender.sendMessage("§aUse: §f/find <player>");
		} else {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			ProxiedPlayer playerSave = (ProxiedPlayer) commandSender;
			if ((player == null) || (player.getServer() == null)) {
				commandSender.sendMessage("§cPlayer offline.");
			} else {
				commandSender.sendMessage("§aO player " + args[0]
						+ "§f foi encontrado no ip " + player.getServer().getInfo().getName() + "§f");

				TextComponent component = new TextComponent("§b[Conectar]");
				if (playerSave.getServer().getInfo().getName() == player.getServer().getInfo().getName()) {
					component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
				} else {
					component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/server " + player.getServer().getInfo().getName()));
				}
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("§aClique para ir até o servidor").create()));

				playerSave.sendMessage(component);
				playerSave.sendMessage("");
			}
		}

	}

	public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
		if ((args.length > 1) || (args.length == 0)) {
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

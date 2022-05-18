package br.com.zenix.core.proxy.commands.staff.message;

import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import br.com.zenix.core.spigot.player.tag.type.TagType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class StaffCommand extends ProxyCommand {

	public StaffCommand() {
		super("stafflist");
	}

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

		if (args.length != 0) {
			sendArgumentMessage(commandSender, "§6§lSTAFFLIST", "/stafflist");
			return;
		}

		proxiedPlayer.sendMessage(new TextComponent("§aLista de membros da equipe online"));
		proxiedPlayer.sendMessage(new TextComponent(" "));

		for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
			if (all.hasPermission("commons.cmd.bungeemod")) {
				String tag = getRanking(all);

				String format = tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "") + ChatColor.WHITE
						+ " §f: §e";
				
				proxiedPlayer.sendMessage(new TextComponent(" §f- " + format + "" + all.getServer().getInfo().getName()));
			}
		}
	}

	public String getRanking(ProxiedPlayer player) {
		ProxyAccount account = getProxyManager().getAccountManager().getAccount(player.getUniqueId());
		return "" + getTag(account.getGroup().getId()).getPrefix() + "" + player.getName();
	}

	public TagType getTag(int id) {
		for (TagType tagType : TagType.values()) {
			if (tagType.getId() == id) {
				return tagType;
			}
		}
		return null;
	}
}

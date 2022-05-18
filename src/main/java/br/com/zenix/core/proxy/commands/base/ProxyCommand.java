package br.com.zenix.core.proxy.commands.base;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;

import br.com.zenix.core.proxy.Proxy;
import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

@SuppressWarnings("deprecation")
public abstract class ProxyCommand extends Command implements TabExecutor {

	public boolean enabled = true;
	public static final String ERROR = "§c§lERROR §f";
	public static final String NO_PERMISSION = "§c§lPERMISSAO §f";
	public static final String OFFLINE = "§c§lOFFLINE §f";

	public ProxyCommand(String name) {
		super(name);
	}

	public ProxyCommand(String name, String... aliases) {
		super(name, null, aliases);
	}

	public abstract void execute(CommandSender commandSender, String[] args);

	public ProxyManager getProxyManager() {
		return Proxy.getProxyManager();
	}

	public Integer getInteger(String string) {
		return Integer.valueOf(string);
	}

	public boolean hasAccount(ProxyAccount account) {
		if (account.getPlayer().hasPermission("commons.cmd.bungeemod")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasModGC(ProxyAccount account) {
		if (account.getPlayer().hasPermission("commons.cmd.bungee") && account.getPlayer().hasPermission("commons.cmd.ss")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasAdmin(ProxyAccount account) {
		if (account.getPlayer().hasPermission("commons.cmd.bungee")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPlayer(CommandSender commandSender) {
		return commandSender instanceof ProxiedPlayer;
	}

	public boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String getArgs(String[] args, int starting) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = starting; i < args.length; i++) {
			stringBuilder.append(args[i] + " ");
		}
		return stringBuilder.toString();
	}

	public static String getError() {
		return ERROR;
	}

	public static String getOffline() {
		return OFFLINE;
	}

	public static String getNoPermission() {
		return NO_PERMISSION;
	}

	public void sendNumericMessage(CommandSender commandSender) {
		commandSender.sendMessage("§cVocê informou um caractere com um número. Números não são permitidos.");
	}

	public void sendPermissionMessage(CommandSender commandSender) {
		commandSender.sendMessage("§cVocê não tem permissão.");
	}

	public void sendExecutorMessage(CommandSender commandSender) {
		commandSender.sendMessage("ERRO: Somente players podem usar esse comando.");
	}

	public void sendArgumentMessage(CommandSender commandSender, String command, String args) {
		commandSender.sendMessage("§aUse: §f" + args);
	}

	public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
		if (args.length == 0) {
			return ImmutableSet.of();
		}
		Set<String> match = new HashSet<>();
		String search = args[0].toLowerCase();
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (player.getName().toLowerCase().startsWith(search)) {
				match.add(player.getName());
			}
		}

		return match;
	}
}
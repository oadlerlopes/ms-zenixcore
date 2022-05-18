package br.com.zenix.core.spigot.commands.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public abstract class BukkitCommand extends Command {

	private CoreManager coreManager;
	public boolean enabled = true;
	public static final String ERROR = "§c§lERROR §f";
	public static final String NO_PERMISSION = "§c§lPERMISSION §f";
	public static final String OFFLINE = "§c§lOFFLINE §f";

	public BukkitCommand(String name) {
		super(name);
	}

	public BukkitCommand(String name, String description) {
		super(name, description, "", new ArrayList<String>());
	}

	public BukkitCommand(String name, String description, List<String> aliases) {
		super(name, description, "", aliases);
	}

	public BukkitCommand(String name, String description, String... aliases) {
		super(name, description, "", Arrays.asList(aliases));
	}

	public abstract boolean execute(CommandSender commandSender, String label, String[] args);

	public CoreManager getCoreManager() {
		this.coreManager = Core.getCoreManager();
		return coreManager;
	}

	public Integer getInteger(String string) {
		return Integer.valueOf(string);
	}

	public boolean isPlayer(CommandSender commandSender) {
		boolean isPlayer = commandSender instanceof Player;
		if (!isPlayer)
			sendExecutorMessage(commandSender);
		return isPlayer;
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

	public boolean hasPermission(CommandSender commandSender, String perm) {
		boolean hasPermission = commandSender.hasPermission("commons.cmd." + perm);
		if (!hasPermission)
			sendPermissionMessage(commandSender);
		return hasPermission;
	}

	public String getArgs(String[] args, int starting) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = starting; i < args.length; i++) {
			stringBuilder.append(args[i] + " ");
		}
		return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
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

	public void sendMessage(CommandSender commandSender, String msg) {
		commandSender.sendMessage("");
		commandSender.sendMessage(msg);
		commandSender.sendMessage("");
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

	public void sendOfflinePlayerMessage(CommandSender commandSender, String player) {
		commandSender.sendMessage(getOffline() + "O player " + player + " está offline.");
	}

	public void sendWarning(String warning) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("commons.admin")) {
				player.sendMessage("§7(!) " + warning + "");
			}
		}
	}

}

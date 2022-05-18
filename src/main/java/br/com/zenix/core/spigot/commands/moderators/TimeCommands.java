package br.com.zenix.core.spigot.commands.moderators;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class TimeCommands extends BukkitCommand {

	public TimeCommands() {
		super("time");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		if (args.length < 2) {
			commandSender.sendMessage("§aUse: §f/time set <dia/noite> | /time add <value>");
			return false;
		}
		if (args[0].equals("set")) {
			int value;
			if (args[1].equals("dia")) {
				value = 0;
			} else {
				if (args[1].equals("noite")) {
					value = 12500;
				} else {
					value = getInteger(commandSender, args[1], 0);
				}
			}
			for (World world : Bukkit.getWorlds()) {
				world.setTime(value);
			}
			commandSender.sendMessage("§aTempo setado para §f" + value);
		} else if (args[0].equals("add")) {
			int value = getInteger(commandSender, args[1], 0);
			for (World world : Bukkit.getWorlds()) {
				world.setFullTime(world.getFullTime() + value);
			}
			commandSender.sendMessage("§aO valor §f" + value + "§a foi adicionado ao tempo");
		} else {
			commandSender.sendMessage("§aUse: §f/time set <dia/noite> | /time add <value>");
		}
		return true;
	}

	protected int getInteger(CommandSender commandSender, String value, int min) {
		return getInteger(commandSender, value, min, Integer.MAX_VALUE);
	}

	public int getInteger(CommandSender commandSender, String value, int min, int max) {
		return getInteger(commandSender, value, min, max, false);
	}

	public int getInteger(CommandSender commandSender, String value, int min, int max, boolean Throws) {
		int i = min;
		try {
			i = Integer.valueOf(value).intValue();
		} catch (NumberFormatException ex) {
			if (Throws) {
				throw new NumberFormatException(String.format("%s is not a valid id", new Object[] { value }));
			}
		}
		if (i < min) {
			i = min;
		} else if (i > max) {
			i = max;
		}
		return i;
	}

	public static class NightCommand extends BukkitCommand {

		public NightCommand() {
			super("noite", "", "night");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			Player player = (Player) commandSender;
			for (World world : Bukkit.getWorlds()) {
				world.setTime(12500);
			}
			player.sendMessage("§cVocê alterou o tempo e o alterou de noite.");

			return true;
		}

	}

	public static class DayCommand extends BukkitCommand {

		public DayCommand() {
			super("dia", "", "day");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			Player player = (Player) commandSender;
			for (World world : Bukkit.getWorlds()) {
				world.setTime(0);
			}
			player.sendMessage("§aVocê alterou o tempo e o tornou de dia.");

			return true;
		}

	}

}
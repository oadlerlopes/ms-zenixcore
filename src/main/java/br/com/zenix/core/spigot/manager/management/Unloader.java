package br.com.zenix.core.spigot.manager.management;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.spigot.manager.CoreManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class Unloader extends Management {

	public Unloader(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		unloadCommands();

		String worldName = "world";
		File file = new File(worldName + "/playerdata");
		File file2 = new File(worldName + "/stats");
		if (file.isDirectory()) {
			String[] playerDat = file.list();
			for (int i = 0; i < playerDat.length; i++) {
				File dat = new File(file, playerDat[i]);
				dat.delete();
			}
		}

		if (file2.isDirectory()) {
			String[] playerDat = file2.list();
			for (int i = 0; i < playerDat.length; i++) {
				File dat = new File(file2, playerDat[i]);
				dat.delete();
			}
		}

		return true;
	}

	private void unloadCommands() {
		String name[] = { "achievement", "playsound", "setidletimeout", "testforblock", "setblock", "summon",
				"tellraw", "icanhasbukkit", "spreadplayers", "netstat", "ban-ip", "banlist", "clear", "defaultgamemode",
				"deop", "difficulty", "gamerule", "help", "kill", "list", "me", "op", "pardon", "pardon-ip", "rl",
				"save-on", "say", "scoreboard", "seed", "spawnpoint", "testfor", "toggledownfall", "ver", "version",
				"weather", "xp", "protocol", "reload", "restart", "w", "?", "ver", "version", "tellraw", "whisper", "bukkit:w", };

		try {
			Field f = getCommandMap().getClass().getDeclaredField("knownCommands");
			f.setAccessible(true);
			final Object list = f.get(getCommandMap());

			new BukkitRunnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					Map<String, Command> commands = (Map<String, Command>) list;
					ArrayList<Command> deleted = new ArrayList<Command>();

					for (Command cmd : commands.values()) {
						for (String s : name)
							if (cmd.getName().equalsIgnoreCase(s)) {
								deleted.add(cmd);
							}
					}

					for (Command d : deleted)
						commands.values().remove(d);
				}
			}.runTaskLater(getCoreManager().getPlugin(), 1);
		} catch (Exception exception) {
			getLogger().log("Ocorreu um erro no unloader - CommandMap");
		}

		try {
			Field f = getCommandMap().getClass().getDeclaredField("fallbackCommands");
			f.setAccessible(true);
			final Object list = f.get(getCommandMap());

			new BukkitRunnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					Set<VanillaCommand> commands = (Set<VanillaCommand>) list;
					ArrayList<Command> deleted = new ArrayList<Command>();

					for (VanillaCommand cmd : commands) {
						for (String s : name)
							if (cmd.getName().equalsIgnoreCase(s)) {
								deleted.add(cmd);
							}
					}

					for (Command d : deleted)
						commands.remove(d);
				}
			}.runTaskLater(getCoreManager().getPlugin(), 1);
		} catch (Exception exception) {
			getLogger().log("Ocorreu um erro no unloader - CommandMap");
		}
	}

	private CommandMap getCommandMap() {
		try {
			Field f = getCoreManager().getPlugin().getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			CommandMap map = (CommandMap) f.get(getCoreManager().getPlugin().getServer());
			return map;
		} catch (Exception exception) {
			getLogger().log("Ocorreu um erro no unloader - CommandMap");
		}
		return null;
	}

}

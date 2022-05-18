package br.com.zenix.core.spigot.manager.managements;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;

import br.com.zenix.core.plugin.utilitaries.loader.Getter;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class ClassLoader extends Management {

	public ClassLoader(CoreManager coreManager) {
		super(coreManager, "ClassLoader");
	}

	public boolean initialize() {
		return load();
	}

	public boolean load() {
		getLogger().log("Starting trying to load all the classes of commands and listeners of the plugin.");

		for (Class<?> classes : Getter.getClassesForPackage(getCoreManager().getPlugin(), "br.com.zenix.core.spigot")) {
			
			try {
				if (BukkitCommand.class.isAssignableFrom(classes) && classes != BukkitCommand.class) {
					BukkitCommand command = (BukkitCommand) classes.newInstance();
					if (command.enabled) {
						try{
						    final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
						    commandMapField.setAccessible(true);
						    CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
						    commandMap.register(command.getName(), command);
						} catch (NoSuchFieldException  | IllegalArgumentException | IllegalAccessException exception){
						    exception.printStackTrace();
						}
					}
				}
			} catch (Exception exception) {
				getLogger().error("Error to load the command " + classes.getSimpleName() + ", stopping the process!",
						exception);
				return false;
			}
			try {
				if (Listener.class.isAssignableFrom(classes)) {
					Listener listener = (Listener) classes.newInstance();
					Bukkit.getPluginManager().registerEvents(listener, getCoreManager().getPlugin());
				}
			} catch (Exception exception) {
				getLogger().error("Error to load the listener " + classes.getSimpleName() + ", stopping the process!",
						exception);
				return false;
			}
		}
		return true;
	}

}

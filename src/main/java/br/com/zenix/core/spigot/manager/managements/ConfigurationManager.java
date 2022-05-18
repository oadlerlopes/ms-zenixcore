package br.com.zenix.core.spigot.manager.managements;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class ConfigurationManager extends Management {

	private static final String CONFIG_FILE_NAME = "config-core.yml";

	private File configFile;
	private FileConfiguration config;

	public ConfigurationManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {

		this.configFile = new File(getCoreManager().getPlugin().getDataFolder(), CONFIG_FILE_NAME);

		if (!configFile.exists()) {
			getLogger().log(Level.WARNING, "The configuration file %s dont exists. Creating...", CONFIG_FILE_NAME);
			getCoreManager().getPlugin().saveResource(CONFIG_FILE_NAME, true);
		}

		this.config = YamlConfiguration.loadConfiguration(configFile);

		return true;
	}

	public File getConfigFile() {
		return configFile;
	}

	public FileConfiguration getConfig() {
		return config;
	}
}

package br.com.zenix.core.spigot.commands.base;

import org.bukkit.event.Listener;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class BukkitListener implements Listener {

	public CoreManager getCoreManager() {
		return Core.getCoreManager();
	}
}


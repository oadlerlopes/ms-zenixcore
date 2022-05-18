package br.com.zenix.core.spigot.player.league;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class LeaguesManager extends Management {
	
	public LeaguesManager(CoreManager coreManager) {
		super(coreManager);
	}
	
	public boolean initialize() {
		return true;
	}

}

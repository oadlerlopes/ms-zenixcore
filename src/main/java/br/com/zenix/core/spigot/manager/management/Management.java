package br.com.zenix.core.spigot.manager.management;

import java.util.logging.Level;

import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.spigot.manager.CoreManager;


/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public abstract class Management {

	private final String name;
	private final Logger logger;
	private final CoreManager manager;
	private boolean correctlyStart = true;

	public Management(CoreManager manager) {
		this.name = getClass().getSimpleName().replace("Manager", "");
		this.manager = manager;
		this.logger = new Logger(manager.getLogger(), name);

		logger.log("Trying to start the " + name + " handler.");

		checkStart(initialize());
	}

	public Management(CoreManager manager, String name) {
		this.name = name;
		this.manager = manager;
		this.logger = new Logger(manager.getLogger(), name);

		logger.log("Trying to start the " + name + " manager.");

		checkStart(initialize());
	}

	public Management(Management manager, String name) {
		this.name = name;
		this.manager = manager.getCoreManager();
		this.logger = new Logger(manager.getLogger(), name);

		logger.log("Trying to start the " + name + " manager.");

		checkStart(initialize());
	}

	public abstract boolean initialize();

	protected boolean checkStart(boolean bool) {
		if (bool) {
			getLogger().log("The " + name + " manager has been started correctly.");
		} else {
			getLogger().log(Level.SEVERE, "The " + name + " manager has been not started correctly, stopping the server.");
			getLogger().log(Level.SEVERE, "The server is going to stop because the manager " + name + " it was not started.");
			getCoreManager().getPlugin().getServer().shutdown();
			getCoreManager().getCore().setCorrectlyStarted(false);
			correctlyStart = false;
		}
		return bool;
	}

	public boolean correctlyStart() {
		return correctlyStart;
	}

	public CoreManager getCoreManager() {
		return manager;
	}

	protected String getName() {
		return name;
	}

	public Logger getLogger() {
		return logger;
	}

}

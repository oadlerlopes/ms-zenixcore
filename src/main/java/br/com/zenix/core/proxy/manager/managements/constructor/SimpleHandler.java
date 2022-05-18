package br.com.zenix.core.proxy.manager.managements.constructor;

import java.util.logging.Level;

import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.proxy.manager.ProxyManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class SimpleHandler {

	private final String name;
	private final Logger logger;
	private final ProxyManager manager;
	private boolean correctlyStart = true;

	public SimpleHandler(ProxyManager manager) {
		this.name = getClass().getSimpleName().replace("Manager", "");
		this.manager = manager;
		this.logger = new Logger(manager.getLogger(), name);

		logger.log("Trying to start the " + name + " handler.");

		checkStart(initialize());
	}

	public SimpleHandler(ProxyManager manager, String name) {
		this.name = name;
		this.manager = manager;
		this.logger = new Logger(manager.getLogger(), name);

		logger.log("Trying to start the " + name + " manager.");

		checkStart(initialize());
	}

	public SimpleHandler(SimpleHandler manager, String name) {
		this.name = name;
		this.manager = manager.getProxyManager();
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
			getProxyManager().getPlugin().getProxy().stop("Error when the plugin Core tried to start.");;
			getProxyManager().getPlugin().setCorrectlyStarted(false);
			correctlyStart = false;
		}
		return bool;
	}

	public boolean correctlyStart() {
		return correctlyStart;
	}

	public ProxyManager getProxyManager() {
		return manager;
	}

	protected String getName() {
		return name;
	}

	public Logger getLogger() {
		return logger;
	}

}

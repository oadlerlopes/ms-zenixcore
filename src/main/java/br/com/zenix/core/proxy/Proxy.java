package br.com.zenix.core.proxy;

import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.proxy.manager.ProxyManager;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class Proxy extends Plugin {

	private static ProxyManager bungeeManager;

	private static Logger logger;
	private boolean correctlyStarted = false;

	public void onEnable() {
		logger = new Logger(getLogger(), null, false);
		bungeeManager = new ProxyManager(this);
	}

	public void onDisable() {
		logger.log("Stopping all thoes activities from the server.");
	}

	public void onLoad() {

	}

	public Logger getLoggerSecondary() {
		return logger;
	}

	public static ProxyManager getProxyManager() {
		return bungeeManager;
	}

	public boolean isCorrectlyStarted() {
		return correctlyStarted;
	}

	public void setCorrectlyStarted(boolean correctlyStarted) {
		this.correctlyStarted = correctlyStarted;
	}

}

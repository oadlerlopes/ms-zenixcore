package br.com.zenix.core.proxy.commands.base;

import br.com.zenix.core.proxy.Proxy;
import br.com.zenix.core.proxy.manager.ProxyManager;
import net.md_5.bungee.api.plugin.Listener;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ProxyListener implements Listener {

	public ProxyManager getProxyManager() {
		return Proxy.getProxyManager();
	}

}

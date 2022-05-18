package br.com.zenix.core.proxy.loader;

import br.com.zenix.core.plugin.utilitaries.loader.Getter;
import br.com.zenix.core.proxy.commands.base.ProxyCommand;
import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ClassLoader extends SimpleHandler {

	public ClassLoader(ProxyManager manager) {
		super(manager);
	}

	public boolean initialize() {

		for (Class<?> classes : Getter.getClassesForPackage(getProxyManager().getPlugin(), "br.com.zenix.core.proxy")) {
			try {
				if (ProxyCommand.class.isAssignableFrom(classes) && classes != ProxyCommand.class) {
					ProxyCommand command = (ProxyCommand) classes.newInstance();
					ProxyServer.getInstance().getPluginManager().registerCommand(getProxyManager().getPlugin(), command);
					getProxyManager().getLogger().debug("Comando " + command.getName() + " carregado!");
				}
			} catch (Exception exception) {
				getProxyManager().getLogger().error("Erro ao carregar o comando " + classes.getSimpleName() + "!", exception);
				return false;
			}

			try {
				Listener listener = null;
				if (Listener.class.isAssignableFrom(classes)) {
					listener = (Listener) classes.newInstance();
				}
				if (listener == null)
					continue;
				ProxyServer.getInstance().getPluginManager().registerListener(getProxyManager().getPlugin(), listener);
				getProxyManager().getLogger().debug("Listener " + listener.getClass().getSimpleName() + " carregado!");

			} catch (Exception exception) {
				getProxyManager().getLogger().error("Erro ao carregar a Listener " + classes.getSimpleName() + "!", exception);
				return false;
			}
		}
		return true;
	}

}

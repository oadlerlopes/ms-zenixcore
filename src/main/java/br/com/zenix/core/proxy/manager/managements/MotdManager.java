package br.com.zenix.core.proxy.manager.managements;

import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class MotdManager extends SimpleHandler {

	private String motdBackup;
	private String motdSet;
	private String prefix, suffix;

	public MotdManager(ProxyManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		prefix = "§f§l";
		suffix = " §f§l";

		motdBackup = "§6§lMODO §f§lPRACTICE §f§lEM FASE §b§lBETA";
		motdSet = "§6§lMODO §f§lPRACTICE §f§lEM FASE §b§lBETA";
		return true;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getMotdBackup() {
		return motdBackup;
	}

	public String getMotdSet() {
		return motdSet;
	}

	public void setMotdBackup(String motdBackup) {
		this.motdBackup = motdBackup;
	}

	public void setMotdSet(String motdSet) {
		this.motdSet = motdSet;
	}

}

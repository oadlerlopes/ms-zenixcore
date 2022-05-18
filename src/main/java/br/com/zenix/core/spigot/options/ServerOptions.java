package br.com.zenix.core.spigot.options;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public enum ServerOptions {

	CHAT(true),
	BUILD(true),
	DROPS(true),
	PVP(true),
	GLOBAL_PVP(true);

	private Boolean active;

	private ServerOptions(Boolean active) {
		this.active = active;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}

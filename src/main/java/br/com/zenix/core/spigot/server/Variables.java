package br.com.zenix.core.spigot.server;

import br.com.zenix.core.spigot.server.minigames.hungergames.ServerEvents;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class Variables {

	public static boolean EVENT = false;
	public static ServerEvents serverEvent;

	public Variables() {
	}

	public String getMessageEvent() {

		if (serverEvent.equals(ServerEvents.MINIZENIX)) {
			return "\nBem vindos a mais um evento aqui no ZenixCC! Estão prontos?\n\nO evento de hoje será um MiniZenix.";
		}

		return "";
	}

}

package br.com.zenix.core.spigot.server.minigames.hungergames;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public enum ServerEvents {

	MINIZENIX("MiniZenix"), 
	NEWHG("NewHG"), 
	ARENAZENIX("ArenaZenix"), 
	MINIZENIXG("MiniZenix - Gangues"), 
	SUMO("Sumo"), 
	LMS("Last Man Standing"), 
	GLADIATOR("Gladiator"), 
	MAZE("Maze"),
	QUIZ("Quiz"), 
	STAFFVSPLAYER("Staff vs Players"), 
	MDR("Mãe de Rua");

	private String name;

	ServerEvents(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static List<String> getEvents() {
		List<String> listToAdd = new ArrayList<>();

		for (ServerEvents serverEvents : values()) {
			listToAdd.add(serverEvents.toString());
		}
		
		return listToAdd;
	}

}

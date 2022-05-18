package br.com.zenix.core.spigot.player.tag.type;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public enum TagType {
	
	OWNER(2, 990, "Dono", "§4§lDONO§4 ", "§4§l"),
	DIRECTOR(4, 976, "Diretor", "§c§lDIRETOR§c ", "§c§l"),
	DEVELOPER(3, 984, "Developer", "§3§lDEV§3 ", "§3§l"),
	ADMINISTRATOR(5, 741, "Admin", "§c§lADMIN§c ", "§c§l"),
	GERENTE(6, 725, "Gerente", "§c§lGERENTE§c ", "§c§l"),
	INVESTIDOR(666, 715, "Investidor", "§e§lINVST§e ", "§E§l"),
	MODERATOR_PLUS(7, 711, "ModPlus", "§5§lMOD+§5 ", "§5§l"),
	MODERATOR_GC(8, 497, "ModGC", "§5§lMODGC§5 ", "§5§l"),
	MODERATOR(10, 482, "Mod", "§5§lMOD§5 ", "§5§l"),
	TRIAL_MODERATOR(11, 468, "Trial", "§5§lTRIAL§5 ", "§5§l"),
	YOUTUBERPLUS(9, 453, "YoutuberPlus", "§3§lYT+§3 ", "§3§l"),
	STAFF(38, 235, "Staff", "§e§lSTAFF§e ", "§e§l"),
	YOUTUBER(13, 223, "Youtuber", "§b§lYOUTUBER§b ", "§b§l"),
	COPA(2686, 210, "Copa", "§e§lCOPA§e ", "§e§l"),
	BETA(14, 208, "Beta", "§1§lBETA§1 ", "§1§l"),
	DESIGNER(20, 193, "Designer", "§2§lDESIGNER§2 ", "§2§l"),
	BUILDER(33, 183, "Builder", "§e§lBUILDER§e ", "§e§l"),
	ELITE(632, 178, "Elite", "§b§lELITE§b ", "§b§l"),
	ULTIMATE(15, 175, "Ultimate", "§d§lULTIMATE§d ", "§d§l"),
	PREMIUM(16, 160, "Premium", "§6§lPREMIUM§6 ", "§6§l"),
	LIGHT(17, 145, "Light", "§a§lLIGHT§a ", "§a§l"),
	MEMBRO(18, 1, "Membro", "§7", "§7§l");
	
	public String tagName, prefix, color;
	public int id, order;

	private TagType(int id, int order, String tagName, String prefix, String color){
		this.id = id;
		this.order = order;
		this.tagName = tagName;
		this.prefix = prefix;
		this.color = color;
	}
	
	public int getId() {
		return id;
	}

	public int getOrder() {
		return order;
	}

	public String getTagName() {
		return tagName;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getColor() {
		return color;
	}

}

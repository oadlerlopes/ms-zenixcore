package br.com.zenix.core.spigot.player.league.type;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public enum LeagueType {

	UNRANKED(1, "UNRANKED", "-", 0, "§f", 14), 
	PRIMARY(2, "Primary", "☰", 1500, "§a", 13), 
	ADVANCED(3, "Advanced", "☲", 3500, "§e", 12), 
	EXPERT(4, "Expert", "☷", 6000, "§1", 11), 
	SILVER(5, "Silver", "✶", 9000, "§7", 10), 
	GOLD(6, "Gold", "✳", 13000, "§6", 9), 
	DIAMOND(7, "Diamond", "✦", 17000, "§b", 8), 
	EMERALD(8, "Emerald", "✥", 22000, "§2", 7), 
	CRYSTAL(9, "Crystal", "❉", 27000, "§9", 6), 
	SAPPHIRE(10, "Sapphire", "❁", 32000, "§3", 5), 
	ELITE(11, "Elite", "✹", 37000, "§5", 4), 
	MASTER(12, "Master", "✫", 42000, "§c", 3), 
	LEGENDARY(13, "Legendary", "✪", 50000, "§4", 2);

	private String name, symbol, color;
	private int id;
	private int xpNumber, order;

	LeagueType(int id, String name, String symbol, int xpNumber, String color, int order) {
		this.id = id;
		this.name = name;
		this.symbol = symbol;
		this.xpNumber = xpNumber;
		this.color = color;
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public int getId() {
		return id;
	}

	public int getXpNumber() {
		return xpNumber;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getColor() {
		return color;
	}

	public static LeagueType getRanked(int score) {
		if (LEGENDARY.getXpNumber() <= score) {
			return LEGENDARY;
		} else if (MASTER.getXpNumber() <= score) {
			return MASTER;
		} else if (ELITE.getXpNumber() <= score) {
			return ELITE;
		} else if (SAPPHIRE.getXpNumber() <= score) {
			return SAPPHIRE;
		} else if (CRYSTAL.getXpNumber() <= score) {
			return CRYSTAL;
		} else if (EMERALD.getXpNumber() <= score) {
			return EMERALD;
		} else if (DIAMOND.getXpNumber() <= score) {
			return DIAMOND;
		} else if (GOLD.getXpNumber() <= score) {
			return GOLD;
		} else if (SILVER.getXpNumber() <= score) {
			return SILVER;
		} else if (EXPERT.getXpNumber() <= score) {
			return EXPERT;
		} else if (ADVANCED.getXpNumber() <= score) {
			return ADVANCED;
		} else if (PRIMARY.getXpNumber() <= score) {
			return PRIMARY;
		} else {
			return UNRANKED;
		}
	}

}

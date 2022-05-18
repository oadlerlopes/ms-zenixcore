package br.com.zenix.core.spigot.player.permissions.type;

import br.com.zenix.core.spigot.player.tag.type.TagType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public enum RankType {
	
	OWNER(2, "Dono", TagType.OWNER),
	DIRECTOR(4, "Diretor", TagType.DIRECTOR),
	DEVELOPER(3, "Developer", TagType.DEVELOPER),
	ADMINISTRATOR(5, "Admin", TagType.ADMINISTRATOR),
	GERENTE(6, "Gerente", TagType.GERENTE),
	INVESTIDOR(666, "Investidor", TagType.INVESTIDOR),
	MODERATOR_PLUS(7, "ModPlus", TagType.MODERATOR_PLUS),
	MODERATOR_GC(8, "ModGC", TagType.MODERATOR_GC),
	YOUTUBERPLUS(9, "YoutuberPlus", TagType.YOUTUBERPLUS),
	MODERATOR(10, "Mod", TagType.MODERATOR),
	TRIAL_MODERATOR(11, "Trial", TagType.TRIAL_MODERATOR),
	YOUTUBER(13, "Youtuber", TagType.YOUTUBER),
	COPA(2686, "Copa", TagType.COPA),
	BETA(14, "Beta", TagType.BETA),
	DESIGNER(20, "Designer", TagType.DESIGNER),
	BUILDER(33, "Builder", TagType.BUILDER),
	ELITE(632, "Elite", TagType.ELITE),
	ULTIMATE(15, "Ultimate", TagType.ULTIMATE),
	PREMIUM(16, "Premium", TagType.PREMIUM),
	LIGHT(17, "Light", TagType.LIGHT),
	MEMBRO(18, "Membro", TagType.MEMBRO);
	
	private String name;
	private int id;
	private TagType tagType;
	
	private RankType(int id, String name, TagType taglist){
		this.id = id;
		this.name = name;
		this.tagType = taglist;
	}
	
	public TagType getTagList() {
		return tagType;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

}

package br.com.zenix.core.spigot.player.cup;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public enum CupGroupType {
	
	NONE(0, "Grupo Nulo", "00 de junho ás 00 horas.", "gn"),
	GROUP_A(1, "Grupo A", "07 de julho ás 13:00 (hr. de brasília)", "ga"),
	GROUP_B(2, "Grupo B", "07 de julho ás 16:00 (hr. de brasília)", "gb"),
	GROUP_C(3, "Grupo C", "08 de julho ás 13:00 (hr. de brasília)", "gc"),
	GROUP_D(4, "Grupo D", "08 de julho ás 16:00 (hr. de brasília)", "gd");
	
	public int id;
	public String name;
	public String sigla;
	public String information;

	CupGroupType(int i, String name, String information, String sigla) {
		this.id = i;
		this.name = name;
		this.information = information;
		this.sigla = sigla;
	}

	public String getName() {
		return name;
	}

	public String getInformation() {
		return information;
	}

	public int getId() {
		return id;
	}
	
	public String getSigla() {
		return sigla;
	}
	
	public static CupGroupType getCupGroupTypeBySigla(String id) {
		CupGroupType type = CupGroupType.NONE;
		for (CupGroupType types : values())
			if (types.getSigla().equalsIgnoreCase(id))
				type = types;
		return type;
	}

	public static CupGroupType getCupGroupType(int id) {
		CupGroupType type = CupGroupType.NONE;
		for (CupGroupType types : values())
			if (types.getId() == id)
				type = types;
		return type;
	}

}

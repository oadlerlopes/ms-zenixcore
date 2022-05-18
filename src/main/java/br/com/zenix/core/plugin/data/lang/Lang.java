package br.com.zenix.core.plugin.data.lang;

import java.util.Arrays;

public enum Lang {

	PT_BR(0, "Português do Brasil"),
	EN_US(1, "English of United States");

	private final int id;
	private final String name;

	Lang(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static Lang getLang(int lang) {
		return Arrays.asList(values()).stream().filter(lan -> lan.getId() == lang).findFirst().orElse(null);
	}

	public static Lang getLang(String lang) {
		return Arrays.asList(values()).stream().filter(lan -> lan.name().equalsIgnoreCase(lang)).findFirst().orElse(null);
	}

}

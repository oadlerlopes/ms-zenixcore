package br.com.zenix.core.plugin.utilitaries;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class SpecialKit {

	private final String name;
	private final ItemStack[] armor, inv;
	private final List<PotionEffect> potions;

	public SpecialKit(String name, ItemStack[] armor, ItemStack[] inv, List<PotionEffect> potions) {
		this.name = name;
		this.armor = armor;
		this.inv = inv;
		this.potions = potions;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public ItemStack[] getInv() {
		return inv;
	}

	public String getName() {
		return name;
	}

	public List<PotionEffect> getPotions() {
		return potions;
	}

}

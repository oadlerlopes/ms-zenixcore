package br.com.zenix.core.spigot.player.particle;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum ParticleType {

	HUGE_EXPLOSION("hugeexplosion"),
	LARGE_EXPLODE("largeexplode"),
	FIREWORKS_SPARK("fireworksSpark"),
	BUBBLE("bubble"),
	SUSPEND("suspend"),
	DEPTH_SUSPEND("depthSuspend"),
	TOWN_AURA("townaura"),
	CRIT("crit"),
	MAGIC_CRIT("magicCrit"),
	MOB_SPELL("mobSpell"),
	MOB_SPELL_AMBIENT("mobSpellAmbient"),
	SPELL("spell"),
	INSTANT_SPELL("instantSpell"),
	WITCH_MAGIC("witchMagic"),
	NOTE("note"),
	PORTAL("portal"),
	ENCHANTMENT_TABLE("enchantmenttable"),
	EXPLODE("explode"),
	FLAME("flame"),
	LAVA("lava"),
	FOOTSTEP("footstep"),
	SPLASH("splash"),
	LARGE_SMOKE("largesmoke"),
	CLOUD("cloud"),
	RED_DUST("reddust"),
	SNOWBALL_POOF("snowballpoof"),
	DRIP_WATER("dripWater"),
	DRIP_LAVA("dripLava"),
	SNOW_SHOVEL("snowshovel"),
	SLIME("slime"),
	HEART("heart"),
	ANGRY_VILLAGER("angryVillager"),
	HAPPY_VILLAGER("happerVillager"),
	ICONCRACK("iconcrack_"),
	TILECRACK("tilecrack_");

	private String particleName;

	ParticleType(String particleName) {
		this.particleName = particleName;
	}

	public void setParticle(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		try {
			setValue(packet, "a", particleName);
			setValue(packet, "b", (float) location.getX());
			setValue(packet, "c", (float) location.getY());
			setValue(packet, "d", (float) location.getZ());
			setValue(packet, "e", offsetX);
			setValue(packet, "f", offsetY);
			setValue(packet, "g", offsetZ);
			setValue(packet, "h", speed);
			setValue(packet, "i", count);
		} catch (Exception e) {
			e.printStackTrace();
		}

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void setParticle(Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			setParticle(player, location, offsetX, offsetY, offsetZ, speed, count);
		}
	}

	public static void setValue(Object instance, String fieldName, Object value) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}

	public static Object getValue(Object instance, String fieldName) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

}
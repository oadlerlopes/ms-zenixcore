package br.com.zenix.core.spigot.player.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GamerHitEntityEvent extends CustomEvent {

	private Player player;
	private LivingEntity entity;
	private double damage;
	private boolean cancel;

	public GamerHitEntityEvent(Player player, LivingEntity entity, double damage) {
		this.player = player;
		this.entity = entity;
		this.damage = damage;
	}

	public Player getPlayer() {
		return player;
	}

	public Player getDamager() {
		return player;
	}

	public LivingEntity getEntity() {
		return this.entity;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isCancelled() {
		return this.cancel;
	}
}

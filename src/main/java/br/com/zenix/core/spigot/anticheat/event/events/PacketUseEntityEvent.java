package br.com.zenix.core.spigot.anticheat.event.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import br.com.zenix.core.spigot.player.events.CustomEvent;

public class PacketUseEntityEvent extends CustomEvent {

	public EntityUseAction action;
	public Player attacker;
	public Entity attacked;

	public PacketUseEntityEvent(EntityUseAction action, Player attacker, Entity attacked) {
		this.action = action;
		this.attacker = attacker;
		this.attacked = attacked;
	}

	public EntityUseAction getAction() {
		return action;
	}

	public Player getAttacker() {
		return attacker;
	}

	public Entity getAttacked() {
		return attacked;
	}

}
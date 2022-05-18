package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.zenix.core.spigot.anticheat.ZencheatManager;
import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilCheat;
import br.com.zenix.core.spigot.anticheat.util.UtilTime;

public class Criticals extends Check {

	public Map<UUID, Entry<Integer, Long>> criticalTicks = new HashMap<>();
	public Map<UUID, Double> fallDistance = new HashMap<>();

	public Criticals() {
		super("Criticals");

		setEnabled(false);
		setBannable(true);
		setMaxViolations(35);
	}

	@EventHandler
	public void onLog(PlayerQuitEvent event) {
		if (!isEnabled())
			return;

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if (criticalTicks.containsKey(uuid)) {
			criticalTicks.remove(uuid);
		}
		if (fallDistance.containsKey(uuid)) {
			criticalTicks.remove(uuid);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!isEnabled())
			return;

		if (!(event.getDamager() instanceof Player)
				|| !event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
			return;
		}

		Player player = (Player) event.getDamager();
		if (player.getAllowFlight() || ZencheatManager.lastVelocity.containsKey(player.getUniqueId())
				|| UtilCheat.slabsNear(player.getLocation()) || player.hasPermission("zenix.bypass")) {
			return;
		}

		Location playerLocation = player.getLocation().clone();
		playerLocation.add(0.0, player.getEyeHeight() + 1.0, 0.0);
		if (UtilCheat.blocksNear(playerLocation)) {
			return;
		}
		int count = 0;
		long time = System.currentTimeMillis();
		if (criticalTicks.containsKey(player.getUniqueId())) {
			count = criticalTicks.get(player.getUniqueId()).getKey();
			time = criticalTicks.get(player.getUniqueId()).getValue();
		}
		if (!fallDistance.containsKey(player.getUniqueId())) {
			return;
		}
		double realFallDistance = fallDistance.get(player.getUniqueId());
		count = player.getFallDistance() > 0.0 && !player.isOnGround() && realFallDistance == 0.0 ? ++count : 0;
		if (criticalTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(time, 10000)) {
			count = 0;
			time = UtilTime.nowlong();
		}
		if (count >= 2) {
			count = 0;
			getCoreManager().getRavenManager().logCheat(this, player, null, Chance.ABSOLUTAMENTE, new String[0]);
		}
		criticalTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(count, time));
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Move(PlayerMoveEvent event) {
		if (!isEnabled())
			return;
		
		Player player = event.getPlayer();
		double falling = 0.0;
		if (!player.isOnGround() && event.getFrom().getY() > event.getTo().getY()) {
			if (fallDistance.containsKey(player.getUniqueId())) {
				falling = fallDistance.get(player.getUniqueId());
			}
			falling += event.getFrom().getY() - event.getTo().getY();
		}
		fallDistance.put(player.getUniqueId(), falling);
	}
}
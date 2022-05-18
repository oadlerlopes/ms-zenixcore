package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilPlayer;
import br.com.zenix.core.spigot.anticheat.util.UtilTime;

public class NoFall extends Check {

	public Map<UUID, Map.Entry<Long, Integer>> noFallTicks = new HashMap<>();
	public Map<UUID, Double> fallDistance = new HashMap<>();
	public List<Player> cancel = new ArrayList<>();

	public NoFall() {
		super("NoFall");

		setEnabled(true);
		setBannable(true);

		setViolationResetTime(120000);
		setViolationsToNotify(15);
		setMaxViolations(35);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if(!isEnabled())
			return;
		
		cancel.add(event.getEntity());
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		if(!isEnabled())
			return;
		
		if (fallDistance.containsKey(event.getPlayer().getUniqueId())) {
			fallDistance.remove(event.getPlayer().getUniqueId());
		}
		if (fallDistance.containsKey(event.getPlayer().getUniqueId())) {
			fallDistance.containsKey(event.getPlayer().getUniqueId());
		}
		if (cancel.contains(event.getPlayer())) {
			cancel.remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if(!isEnabled())
			return;
		
		if (event.getCause() == TeleportCause.ENDER_PEARL) {
			cancel.add(event.getPlayer());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Move(PlayerMoveEvent event) {
		if(!isEnabled())
			return;
		
		Player player = event.getPlayer();
		if (player.getAllowFlight() || player.getGameMode().equals(GameMode.CREATIVE) || player.getVehicle() != null
				|| cancel.remove(player) || UtilPlayer.isOnClimbable(player, 0) || UtilPlayer.isInWater(player)) {
			return;
		}
		Damageable dplayer = (Damageable) event.getPlayer();

		if (dplayer.getHealth() <= 0.0D) {
			return;
		}

		double falling = 0.0D;
		if ((!UtilPlayer.isOnGround(player)) && (event.getFrom().getY() > event.getTo().getY())) {
			if (fallDistance.containsKey(player.getUniqueId())) {
				falling = fallDistance.get(player.getUniqueId()).doubleValue();
			}
			falling += event.getFrom().getY() - event.getTo().getY();
		}
		fallDistance.put(player.getUniqueId(), Double.valueOf(falling));
		if (falling < 3.0D) {
			return;
		}
		
		long time = System.currentTimeMillis();
		int count = 0;
		if (noFallTicks.containsKey(player.getUniqueId())) {
			time = noFallTicks.get(player.getUniqueId()).getKey().longValue();
			count = noFallTicks.get(player.getUniqueId()).getValue().intValue();
		}
		if ((player.isOnGround()) || (player.getFallDistance() == 0.0F)) {
			player.damage(5);
			count += 2;
		} else {
			count--;
		}
		if (noFallTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(time, 10000L)) {
			count = 0;
			time = System.currentTimeMillis();
		}
		if (count >= 4) {
			count = 0;

			fallDistance.put(player.getUniqueId(), Double.valueOf(0.0D));
			getCoreManager().getRavenManager().logCheat(this, player, "Packet NoFall", Chance.ABSOLUTAMENTE,
					new String[0]);
		}
		
		noFallTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Long, Integer>(time, count));
		return;
	}

}
package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.zenix.core.spigot.anticheat.ZencheatManager;
import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilCheat;
import br.com.zenix.core.spigot.anticheat.util.UtilMath;
import br.com.zenix.core.spigot.anticheat.util.UtilPlayer;
import br.com.zenix.core.spigot.anticheat.util.UtilTime;

public class Speed extends Check {

	public Map<UUID, Map.Entry<Integer, Long>> speedTicks = new HashMap<>();
	public Map<UUID, Map.Entry<Integer, Long>> tooFastTicks = new HashMap<>();
	public Map<UUID, Long> lastHit = new HashMap<>();
	public Map<UUID, Double> velocity = new HashMap<>();

	public Speed() {
		super("Speed");

		setEnabled(true);
		setBannable(true);
		setViolationsToNotify(15);
		setMaxViolations(35);
	}

	@EventHandler(ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		if (!isEnabled())
			return;

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			lastHit.put(player.getUniqueId(), System.currentTimeMillis());
		}
	}

	public boolean isOnIce(final Player player) {
		Location location = player.getLocation();
		location.setY(location.getY() - 1.0);

		if (location.getBlock().getType().equals((Object) Material.ICE)) {
			return true;
		}

		location.setY(location.getY() - 1.0);

		return location.getBlock().getType().equals((Object) Material.ICE);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLog(PlayerQuitEvent event) {
		if (!isEnabled())
			return;

		if (speedTicks.containsKey(event.getPlayer().getUniqueId())) {
			speedTicks.remove(event.getPlayer().getUniqueId());
		}
		if (tooFastTicks.containsKey(event.getPlayer().getUniqueId())) {
			tooFastTicks.remove(event.getPlayer().getUniqueId());
		}
		if (lastHit.containsKey(event.getPlayer().getUniqueId())) {
			lastHit.remove(event.getPlayer().getUniqueId());
		}
		if (velocity.containsKey(event.getPlayer().getUniqueId())) {
			velocity.remove(event.getPlayer().getUniqueId());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void CheckSpeed(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if ((event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getY() == event.getTo().getY())
				&& (event.getFrom().getZ() == event.getFrom().getZ()) || player.getAllowFlight()
				|| player.getVehicle() != null
				|| player.getVelocity().length() + 0.1 < velocity.getOrDefault(player.getUniqueId(), -1.0D)
				|| (ZencheatManager.lastVelocity.containsKey(player.getUniqueId())
						&& !player.hasPotionEffect(PotionEffectType.POISON)
						&& !player.hasPotionEffect(PotionEffectType.WITHER) && player.getFireTicks() == 0)) {
			return;
		}

		long lastHitDiff = lastHit.containsKey(player.getUniqueId())
				? lastHit.get(player.getUniqueId()) - System.currentTimeMillis() : 2001L;

		int count = 0;
		long time = UtilTime.nowlong();
		if (speedTicks.containsKey(player.getUniqueId())) {
			count = speedTicks.get(player.getUniqueId()).getKey().intValue();
			time = speedTicks.get(player.getUniqueId()).getValue().longValue();
		}

		int tooFastCount = 0;
		double percent = 0D;
		if (tooFastTicks.containsKey(player.getUniqueId())) {
			double offsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(event.getFrom().toVector()),
					UtilMath.getHorizontalVector(event.getTo().toVector()));
			double limitXZ = 0.0D;

			if ((UtilPlayer.isOnGround(player)) && (player.getVehicle() == null)) {
				limitXZ = 0.34D;
			} else {
				limitXZ = 0.39D;
			}

			if (lastHitDiff < 800L) {
				++limitXZ;
			} else if (lastHitDiff < 1600L) {
				limitXZ += 0.4;
			} else if (lastHitDiff < 2000L) {
				limitXZ += 0.1;
			}

			if (UtilCheat.slabsNear(player.getLocation())) {
				limitXZ += 0.05D;
			}

			Location location = UtilPlayer.getEyeLocation(player);
			location.add(0.0D, 1.0D, 0.0D);
			if ((location.getBlock().getType() != Material.AIR) && (!UtilCheat.canStandWithin(location.getBlock()))) {
				limitXZ = 0.69D;
			}

			Location below = event.getPlayer().getLocation().clone().add(0.0D, -1.0D, 0.0D);

			if (UtilCheat.isStair(below.getBlock())) {
				limitXZ += 0.6;
			}

			if (isOnIce(player)) {
				if ((location.getBlock().getType() != Material.AIR)
						&& (!UtilCheat.canStandWithin(location.getBlock()))) {
					limitXZ = 1.0D;
				} else {
					limitXZ = 0.75D;
				}
			}

			float speed = player.getWalkSpeed();
			limitXZ += (speed > 0.2F ? speed * 10.0F * 0.33F : 0.0F);

			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.SPEED)) {
					if (player.isOnGround()) {
						limitXZ += 0.061D * (effect.getAmplifier() + 1);
					} else {
						limitXZ += 0.031D * (effect.getAmplifier() + 1);
					}
				}
			}

			if (offsetXZ > limitXZ
					&& !UtilTime.elapsed(tooFastTicks.get(player.getUniqueId()).getValue().longValue(), 150L)) {
				percent = (offsetXZ - limitXZ) * 100;
				tooFastCount = tooFastTicks.get(player.getUniqueId()).getKey().intValue() + 3;
			} else {
				tooFastCount = tooFastCount > -150 ? tooFastCount-- : -150;
			}
		}
		
		if (tooFastCount >= 11) {
			tooFastCount = 0;
			count++;
			getCoreManager().getLogger().log("" + player + ", New Count: " + count);
		}
		
		if (speedTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(time, 30000L)) {
			count = 0;
			time = UtilTime.nowlong();
		}
		
		Chance prob = Chance.PROVAVELMENTE;
		if (count >= 3) {
			prob = Chance.ABSOLUTAMENTE;
			getCoreManager().getLogger().log("" + player + " Logged for Speed. Count: " + count);
			count = 0;
			getCoreManager().getRavenManager().logCheat(this, player, Math.round(percent) + "% faster than normal",
					prob, new String[0]);
		}
		
		if (!player.isOnGround()) {
			velocity.put(player.getUniqueId(), player.getVelocity().length());
		} else {
			velocity.put(player.getUniqueId(), -1.0D);
		}
		
		tooFastTicks.put(player.getUniqueId(),
				new AbstractMap.SimpleEntry<Integer, Long>(tooFastCount, System.currentTimeMillis()));
		speedTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(count, time));
	}
}
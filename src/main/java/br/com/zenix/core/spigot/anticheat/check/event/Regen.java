package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;

import br.com.zenix.core.spigot.anticheat.ZencheatManager;
import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilPlayer;
import br.com.zenix.core.spigot.anticheat.util.UtilTime;

public class Regen extends Check {

	public Map<UUID, Long> lastHeal = new HashMap<>();
	public Map<UUID, Entry<Integer, Long>> fastHealTicks = new HashMap<>();

	public Regen() {
		super("Regen");

		setEnabled(true);
		setBannable(true);
		setViolationsToNotify(15);
		setMaxViolations(35);
		setViolationResetTime(60000L);
	}

	@EventHandler
	public void onLog(PlayerQuitEvent event) {
		if (!isEnabled())
			return;

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if (lastHeal.containsKey(uuid)) {
			lastHeal.remove(uuid);
		}
		if (fastHealTicks.containsKey(uuid)) {
			fastHealTicks.remove(uuid);
		}
	}

	public boolean checkFastHeal(Player player) {
		if (lastHeal.containsKey(player.getUniqueId())) {
			long l = lastHeal.get(player.getUniqueId()).longValue();
			lastHeal.remove(player.getUniqueId());
			if (System.currentTimeMillis() - l < 3000L) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onHeal(EntityRegainHealthEvent event) {
		if (!isEnabled())
			return;

		if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)
				|| !(event.getEntity() instanceof Player)
				|| getCoreManager().getLag().getTPS() < ZencheatManager.tpsToCancel) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
			return;
		}
		
		int count = 0;
		long time = System.currentTimeMillis();
		
		if (fastHealTicks.containsKey(player.getUniqueId())) {
			count = fastHealTicks.get(player.getUniqueId()).getKey().intValue();
			time = fastHealTicks.get(player.getUniqueId()).getValue().longValue();
		}
		
		if (checkFastHeal(player) && !UtilPlayer.isFullyStuck(player) && !UtilPlayer.isPartiallyStuck(player)) {
			count++;
		} else {
			count = count > 0 ? count - 1 : count;
		}

		if (count > 2) {
			getCoreManager().getRavenManager().logCheat(this, player, null, Chance.ABSOLUTAMENTE, new String[0]);
		}
		
		if (fastHealTicks.containsKey(player.getUniqueId()) && UtilTime.elapsed(time, 60000L)) {
			count = 0;
			time = UtilTime.nowlong();
		}
		
		lastHeal.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
		fastHealTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<Integer, Long>(count, time));
	}
}
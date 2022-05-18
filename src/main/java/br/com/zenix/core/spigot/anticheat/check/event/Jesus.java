package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilCheat;

public class Jesus extends Check {

	public Map<Player, Integer> onWater = new HashMap<>();
	public List<Player> placedBlockOnWater = new ArrayList<>();
	public Map<Player, Integer> countJesus = new HashMap<>();
	public Map<Player, Long> velocity = new HashMap<>();

	public Jesus() {
		super("Jesus");

		setEnabled(true);
		setBannable(true);
		setViolationsToNotify(15);
		setMaxViolations(35);

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		if (!isEnabled())
			return;

		if (placedBlockOnWater.contains(event.getPlayer())) {
			placedBlockOnWater.remove(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		if (!isEnabled())
			return;

		if (onWater.containsKey(event.getEntity())) {
			onWater.remove(event.getEntity());
		}
		if (placedBlockOnWater.contains(event.getEntity())) {
			placedBlockOnWater.remove(event.getEntity());
		}
		if (countJesus.containsKey(event.getEntity())) {
			countJesus.remove(event.getEntity());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVelocity(PlayerVelocityEvent event) {
		if (!isEnabled())
			return;

		velocity.put(event.getPlayer(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (!isEnabled())
			return;

		if (event.getBlockReplacedState().getBlock().getType() == Material.WATER) {
			placedBlockOnWater.add(event.getPlayer());
		}
	}

	@EventHandler
	public void checkJesus(PlayerMoveEvent event) {
		if (!isEnabled())
			return;

		Player player = event.getPlayer();

		if (event.isCancelled()
				|| (event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getZ() == event.getTo().getZ())
				|| player.getAllowFlight() || UtilCheat.isOnLilyPad(player)
				|| player.getLocation().clone().add(0.0D, 0.4D, 0.0D).getBlock().getType().isSolid()
				|| placedBlockOnWater.remove(player)) {
			return;
		}

		int count = countJesus.getOrDefault(player, 0);

		if ((UtilCheat.cantStandAtWater(player.getWorld().getBlockAt(player.getLocation())))
				&& (UtilCheat.isHoveringOverWater(player.getLocation()))
				&& (!UtilCheat.isFullyInWater(player.getLocation()))) {
			count += 2;
		} else {
			count = count > 0 ? count - 1 : count;
		}

		if (count > 19) {
			count = 0;
			getCoreManager().getRavenManager().logCheat(this, player, null, Chance.ABSOLUTAMENTE, new String[0]);
		}

		countJesus.put(player, count);
	}

}
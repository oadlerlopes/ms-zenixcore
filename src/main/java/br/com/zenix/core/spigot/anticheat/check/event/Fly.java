package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.zenix.core.spigot.anticheat.ZencheatManager;
import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilCheat;
import br.com.zenix.core.spigot.anticheat.util.UtilMath;
import br.com.zenix.core.spigot.anticheat.util.UtilPlayer;

public class Fly extends Check {

	public static Map<UUID, Long> flyTicks = new HashMap<>();

	public Fly() {
		super("Fly");

		setEnabled(true);
		setBannable(true);
		setViolationsToNotify(15);
		setMaxViolations(35);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (!isEnabled())
			return;

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if (flyTicks.containsKey(uuid)) {
			flyTicks.remove(uuid);
		}
	}

	@EventHandler
	public void CheckFlyA(PlayerMoveEvent event) {
		if(!isEnabled())
			return;
		
		Player player = event.getPlayer();

		getCoreManager().getRavenManager();
		if (event.isCancelled()
				|| (event.getTo().getX() == event.getFrom().getX()) && (event.getTo().getZ() == event.getFrom().getZ())
				|| player.getAllowFlight() || player.getVehicle() != null
				|| getCoreManager().getLag().getTPS() < ZencheatManager.tpsToCancel
				|| UtilPlayer.isInWater(player) || UtilPlayer.isInSponge(player) || UtilCheat.isInWeb(player)) {
			return;
		}

		if (UtilCheat.blocksNear(player.getLocation())) {
			if (flyTicks.containsKey(player.getUniqueId())) {
				flyTicks.remove(player.getUniqueId());
			}
			return;
		}
		if (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0.06) {
			if (flyTicks.containsKey(player.getUniqueId())) {
				flyTicks.remove(player.getUniqueId());
			}
			return;
		}

		long time = System.currentTimeMillis();
		if (flyTicks.containsKey(player.getUniqueId())) {
			time = flyTicks.get(player.getUniqueId()).longValue();
		}
		
		long ms = System.currentTimeMillis() - time;
		
		if (player.getLocation().getBlock().getType() == Material.SPONGE){
			return;
		}
		
		if (ms > 200L) {
			getCoreManager().getRavenManager().logCheat(this, player,
					"Hovering for " + UtilMath.trim(1, Double.valueOf((ms / 1000))) + " second(s)", Chance.ABSOLUTAMENTE,
					new String[0]);
			flyTicks.remove(player.getUniqueId());
			return;
		}
		
		flyTicks.put(player.getUniqueId(), time);
	}

}
package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.util.UtilCheat;

public class Glide extends Check {

	public Map<UUID, Long> flyTicks = new HashMap<>();

	public Glide() {
		super("Glide");

		setEnabled(false);
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
	public void onMove(PlayerMoveEvent event) {
		if (!isEnabled())
			return;

		Player player = event.getPlayer();

		if (event.isCancelled()
				|| !(event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ())
				|| player.getVehicle() != null || player.getAllowFlight() || UtilCheat.isInWeb(player)) {
			return;
		}

		if (UtilCheat.blocksNear(player)) {
			if (flyTicks.containsKey(player.getUniqueId())) {
				flyTicks.remove(player.getUniqueId());
			}
			return;
		}

		double offsetY = event.getFrom().getY() - event.getTo().getY();
		if (offsetY <= 0.0 || offsetY > 0.16) {
			if (flyTicks.containsKey(player.getUniqueId())) {
				flyTicks.remove(player.getUniqueId());
			}
			return;
		}

		long time = System.currentTimeMillis();
		if (flyTicks.containsKey(player.getUniqueId())) {
			time = flyTicks.get(player.getUniqueId());
		}

		long ms = System.currentTimeMillis() - time;
		if (ms > 1000L) {
			flyTicks.remove(player.getUniqueId());
			if (getCoreManager().getLag().getPing(player) < 275) {
				getCoreManager().getRavenManager().logCheat(this, player, null, Chance.PROVAVELMENTE,
						new String[0]);
			}
			return;
		}

		flyTicks.put(player.getUniqueId(), time);
	}
}
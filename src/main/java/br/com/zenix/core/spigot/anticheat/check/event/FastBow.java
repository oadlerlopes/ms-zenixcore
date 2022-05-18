package br.com.zenix.core.spigot.anticheat.check.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.zenix.core.spigot.anticheat.ZencheatManager.Chance;
import br.com.zenix.core.spigot.anticheat.check.Check;

public class FastBow extends Check {

	public Map<UUID, Long> bowPull = new HashMap<>();
	public Map<UUID, Integer> count = new HashMap<>();

	public FastBow() {
		super("FastBow");

		setViolationsToNotify(15);
		setMaxViolations(35);

		setEnabled(true);
		setBannable(true);
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		if (!isEnabled())
			return;

		Player player = event.getPlayer();
		if (player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.BOW)) {
			bowPull.put(player.getUniqueId(), System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		if (!isEnabled())
			return;
		
		if (bowPull.containsKey(event.getPlayer().getUniqueId())) {
			bowPull.remove(event.getPlayer().getUniqueId());
		}

		if (count.containsKey(event.getPlayer().getUniqueId())) {
			count.remove(event.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onShoot(ProjectileLaunchEvent event) {
		if (!isEnabled()) {
			return;
		}

		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();

				if (bowPull.containsKey(player.getUniqueId())) {

					Long time = System.currentTimeMillis() - this.bowPull.get(player.getUniqueId());
					double power = arrow.getVelocity().length();
					Long timeLimit = 300L;
					int countBow = 0;

					if (count.containsKey(player.getUniqueId())) {
						countBow = count.get(player.getUniqueId());
					}

					if (power > 2.5 && time < timeLimit) {
						count.put(player.getUniqueId(), countBow + 1);
					} else {
						count.put(player.getUniqueId(), countBow > 0 ? countBow - 1 : countBow);
					}

					if (countBow > 8) {
						getCoreManager().getRavenManager().logCheat(this, player, time + " ms", Chance.ABSOLUTAMENTE,
								new String[0]);
						count.remove(player.getUniqueId());
					} else if (countBow > 3) {
						getCoreManager().getRavenManager().logCheat(this, player, time + " ms", Chance.PROVAVELMENTE,
								new String[0]);
						count.remove(player.getUniqueId());
					}
				}
			}
		}
	}
}
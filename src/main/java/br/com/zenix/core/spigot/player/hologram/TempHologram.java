package br.com.zenix.core.spigot.player.hologram;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import br.com.zenix.core.spigot.Core;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class TempHologram implements Listener {

	public static final HashMap<UUID, Hologram> holograms = new HashMap<UUID, Hologram>();
	private static final HashMap<UUID, String[]> holoText = new HashMap<UUID, String[]>();

	public static void addTempHologram(final Player player, String text[], int seconds) {
		if (holograms.containsKey(player.getUniqueId())) {
			return;
		}
		final Hologram hologram = new Hologram(text[0], getFrontLocation(player.getLocation().clone()), false);
		for (int i = 1; i < text.length; ++i) {
			hologram.addLine(text[i]);
		}
		holograms.put(player.getUniqueId(), hologram);
		holoText.put(player.getUniqueId(), text);
		hologram.show(player);
		new BukkitRunnable() {
			public void run() {
				hologram.hide(player);
				hologram.remove();
				holograms.remove(player.getUniqueId());
			}
		}.runTaskLater(Core.getPlugin(Core.class), seconds * 20);
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if (holograms.containsKey(e.getPlayer().getUniqueId())) {
			Hologram hologram = holograms.get(e.getPlayer().getUniqueId());
			hologram.teleport(getFrontLocation(e.getTo().clone()));
			int i = 0;
			for (String holo : holoText.get(e.getPlayer().getUniqueId())) {
				i++;
				for (Hologram h : hologram.getLines()) {
					if (h.getText().equalsIgnoreCase(holo)) {
						h.teleport(getFrontLocation(e.getTo().clone()).add(0, 0.25 * i, 0));
					}
				}
			}
		}
	}

	private static Location getFrontLocation(Location loc) {
		return loc.toVector().add(loc.getDirection().multiply(3).subtract(new Vector(0, 1, 0))).toLocation(loc.getWorld());
	}
}
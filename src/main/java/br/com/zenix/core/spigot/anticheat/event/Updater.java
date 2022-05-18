package br.com.zenix.core.spigot.anticheat.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import br.com.zenix.core.spigot.Core;

public class Updater implements Runnable {
	
	private int updater;

	public Updater() {
		this.updater = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) Core.getCoreManager().getPlugin(), (Runnable) this, 0, 1);
	}

	public void Disable() {
		Bukkit.getScheduler().cancelTask(this.updater);
	}

	@Override
	public void run() {
		UpdateType[] arrupdateType = UpdateType.values();
		int n = arrupdateType.length;
		int n2 = 0;
		while (n2 < n) {
			UpdateType updateType = arrupdateType[n2];
			if (updateType != null && updateType.Elapsed()) {
				try {
					UpdateEvent event = new UpdateEvent(updateType);
					Bukkit.getPluginManager().callEvent((Event) event);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			++n2;
		}
	}
}
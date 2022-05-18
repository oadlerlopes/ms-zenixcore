package br.com.zenix.core.spigot.anticheat.lag;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

public class LagCore extends Management {

	private double tps;

	public LagCore(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		new BukkitRunnable() {
			long sec;
			long currentSec;
			int ticks;

			public void run() {
				this.sec = (System.currentTimeMillis() / 1000L);
				if (this.currentSec == this.sec) {
					this.ticks += 1;
				} else {
					this.currentSec = this.sec;
					LagCore.this.tps = (LagCore.this.tps == 0.0D ? this.ticks : (LagCore.this.tps + this.ticks) / 2.0D);
					this.ticks = 0;
				}
			}
		}.runTaskTimerAsynchronously(getCoreManager().getPlugin(), 1L, 1L);
		return true;
	}

	public double getTPS() {
		return this.tps + 1.0D > 20.0D ? 20.0D : this.tps + 1.0D;
	}

	public double getLag() {
		return Math.round((1.0D - tps / 20.0D) * 100.0D);
	}

	public double getFreeRam() {
		return Math.round(Runtime.getRuntime().freeMemory() / 1000000);
	}

	public double getMaxRam() {
		return Math.round(Runtime.getRuntime().maxMemory() / 1000000);
	}

	public int getPing(Player who) {
		return ((CraftPlayer) who).getHandle().ping;
	}
}
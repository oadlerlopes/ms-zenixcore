package br.com.zenix.core.spigot.anticheat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.anticheat.check.Check;
import br.com.zenix.core.spigot.anticheat.check.event.Criticals;
import br.com.zenix.core.spigot.anticheat.check.event.FastBow;
import br.com.zenix.core.spigot.anticheat.check.event.Fly;
import br.com.zenix.core.spigot.anticheat.check.event.Jesus;
import br.com.zenix.core.spigot.anticheat.check.event.NoFall;
import br.com.zenix.core.spigot.anticheat.check.event.Regen;
import br.com.zenix.core.spigot.anticheat.check.event.Speed;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

public class ZencheatManager extends Management {

	public static List<Check> checks = new ArrayList<>();
	public static Map<UUID, Map<Check, Integer>> violations = new HashMap<>();
	public static Map<UUID, Map<Check, Long>> violationReset = new HashMap<>();
	public static List<UUID> alerts = new ArrayList<>();
	public static Map<Player, Entry<Check, Long>> autoBan = new HashMap<>();
	public static Map<String, Check> namesBanned = new HashMap<>();
	public static Map<UUID, Long> lastVelocity = new HashMap<>();

	public static int pingToCancel = 300, tpsToCancel = 17;

	public ZencheatManager(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		checks.add(new Fly());
		checks.add(new Criticals());
		checks.add(new Speed());
		checks.add(new Regen());
		checks.add(new NoFall());
		checks.add(new Jesus());
		checks.add(new FastBow());

		return true;
	}

	public void logCheat(Check check, Player player, String hoverabletext, Chance chance, String... identifiers) {
		String a = "";
		if (identifiers != null) {
			for (String b : identifiers) {
				a = a + " (" + b + ")";
			}
		}

		addViolation(player, check);
		int violations = getViolations(player, check);

		if ((((CraftPlayer) player).getHandle()).ping > 300) {
			return;
		}
		
		if (violations >= 5) { 
			for (Player players : Bukkit.getOnlinePlayers()) {
				if (players.hasPermission("commons.cmd.ban")) {
					players.sendMessage("§c(AntiCheat) " + player.getName() + ", " + chance + ", " + check.getName() + ", " + violations + "VL");
					
					players.playSound(player.getLocation(), Sound.GHAST_SCREAM, 30.0f, 30.0f);
				}
			}
		}

		if (violations >= check.getViolationsToNotify()) {


			if (violations > check.getMaxViolations() && check.isBannable()) {

				Account account = getCoreManager().getAccountManager().getAccount(player);

				int time = 1800;

				for (PunishRecord punish : account.getPunishRecords().values()) {
					if (punish != null)
						if (punish.getType().equals(PunishType.BAN) || punish.getType().equals(PunishType.TEMPBAN)) {
							if (punish.getMotive().contains("ZenCheat - Suspeita")) {
								time = time + 600;
							}
						}
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						"tempban " + player.getName() + " " + time + "s ZenCheat - Suspeita");
			}
		}
	}

	public int getViolations(Player player, Check check) {
		if (violations.containsKey(player.getUniqueId())) {
			return violations.get(player.getUniqueId()).get(check);
		}
		return 0;
	}

	public Map<Check, Integer> getViolations(Player player) {
		if (violations.containsKey(player.getUniqueId())) {
			return new HashMap<Check, Integer>(violations.get(player.getUniqueId()));
		}
		return null;
	}

	public void addViolation(Player player, Check check) {
		Map<Check, Integer> map = new HashMap<Check, Integer>();
		if (violations.containsKey(player.getUniqueId())) {
			map = violations.get(player.getUniqueId());
		}
		if (!map.containsKey(check)) {
			map.put(check, 1);
		} else {
			map.put(check, map.get(check) + 1);
		}
		violations.put(player.getUniqueId(), map);
	}

	public void removeViolations(Player player) {
		violations.remove(player.getUniqueId());
	}

	public static enum Chance {

		PROVAVELMENTE, ABSOLUTAMENTE;

	}

}

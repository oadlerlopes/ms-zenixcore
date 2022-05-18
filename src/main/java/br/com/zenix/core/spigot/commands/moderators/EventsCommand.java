package br.com.zenix.core.spigot.commands.moderators;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import br.com.zenix.core.plugin.data.handler.type.DataType;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.plugin.utilitaries.SpecialKit;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.options.ServerOptions;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class EventsCommand extends BukkitCommand {

	public static final HashMap<String, SpecialKit> kits = new HashMap<>();

	public EventsCommand() {
		super("skit");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return true;
		}
		if (!hasPermission(commandSender, "skit")) {
			return true;
		}

		if (args.length == 0 || args.length > 3) {

			commandSender.sendMessage("§6§lSKIT §fUse: /skit <create/apply/give/kits> <nome> <radio>");
			return true;
		}

		if (args.length == 1) {
			if (!args[0].equalsIgnoreCase("kits")) {
				commandSender.sendMessage("§6§lSKIT §fUse: /skit <create/apply/give/kits> <nome> <radio/player>");
				return true;
			}
			String kitsMessage = "";
			for (SpecialKit skits : kits.values()) {
				kitsMessage = (kitsMessage.length() == 0 ? "" : ", ") + skits.getName();
			}
			commandSender.sendMessage("§6§lSKIT §fKits disponiveis §e§l" + kitsMessage);
			return true;
		}

		if (args.length == 2) {
			if (!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("give")) {
				commandSender.sendMessage("§6§lSKIT §fUse: /skit <create/apply/give/kits> <nome> <radio/player>");
				return true;
			}
			Player player = (Player) commandSender;
			if (args[0].equalsIgnoreCase("create")) {

				SpecialKit skit = new SpecialKit(args[1], player.getInventory().getArmorContents(),
						player.getInventory().getContents(), (List<PotionEffect>) player.getActivePotionEffects());
				kits.put(skit.getName(), skit);
				player.sendMessage("§6§lSKIT §fVocê criou o skit §e§l" + skit.getName());
				return true;
			}
		}

		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("apply")) {
				Player player = (Player) commandSender;
				if (!kits.containsKey(args[1])) {
					player.sendMessage("§6§lSKIT §fO skit §e§l" + args[1] + "§f não existe.");
					return true;
				}

				if (!isInteger(args[2])) {
					sendNumericMessage(player);
					return true;
				}

				SpecialKit skit = kits.get(args[1]);
				Integer range = Integer.valueOf(args[2]);

				if (range >= 1000) {
					range = 999;
				}

				for (Entity ent : player.getNearbyEntities(range.intValue(), range.intValue(), range.intValue())) {
					if (!(ent instanceof Player))
						continue;
					Player players = (Player) ent;
					players.getInventory().setArmorContents(null);
					players.getInventory().setArmorContents(skit.getArmor());
					players.getInventory().setContents(skit.getInv());
					players.addPotionEffects(skit.getPotions());
					players.sendMessage("§6§lSKIT §fVocê §a§lRECEBEU§f seu KIT!");
				}

				player.sendMessage(
						"§6§lSKIT §fVocê aplicou o skit §e§l" + skit.getName() + "§f em um raio de §a" + range);
				return true;
			} else if (args[0].equalsIgnoreCase("give")) {
				Player player = (Player) commandSender;
				if (!kits.containsKey(args[1])) {
					player.sendMessage("§6§lSKIT §fO skit §e§l" + args[1] + "§f não existe.");
					return true;
				}

				Player sujeito;
				SpecialKit skit = kits.get(args[1]);

				if (Bukkit.getPlayer(args[2]) == null) {
					player.sendMessage("§6§lSKIT §fO player está offline.");
					return true;
				} else {
					sujeito = Bukkit.getPlayer(args[2]);
				}

				sujeito.getInventory().setArmorContents(null);
				sujeito.getInventory().setArmorContents(skit.getArmor());
				sujeito.getInventory().setContents(skit.getInv());
				sujeito.addPotionEffects(skit.getPotions());
				player.sendMessage("§6§lSKIT §fO kit foi setado.");
			} else {
				commandSender.sendMessage("§6§lSKIT §fUse: /skit <create/apply/give/kits> <nome> <radio/player>");
				return true;
			}

		}

		return false;
	}

	public void createKit(String name, ItemStack[] items, ItemStack[] armor, List<PotionEffect> effects) {
		SpecialKit skit = new SpecialKit(name, items, armor, effects);
		kits.put(skit.getName(), skit);
	}

	public static class DamageCommand extends BukkitCommand implements Listener {

		public DamageCommand() {
			super("dano", "Change the option of damage in the server.");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}
			if (!hasPermission(commandSender, "dano")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("global")) {
					ServerOptions.GLOBAL_PVP.setActive(!ServerOptions.GLOBAL_PVP.isActive());
					Bukkit.broadcastMessage("§7Dano global: " + (ServerOptions.GLOBAL_PVP.isActive()
							? "§aativado".toUpperCase() : "§cdesativado".toUpperCase()));
					return true;
				} else if (args[0].equalsIgnoreCase("pvp")) {
					ServerOptions.PVP.setActive(!ServerOptions.PVP.isActive());
					Bukkit.broadcastMessage("§7PvP global: "
							+ (ServerOptions.PVP.isActive() ? "§aativado".toUpperCase() : "§cdesativado")
									.toUpperCase());
					return true;
				} else {
					commandSender.sendMessage("§aUse: §f/dano <global/pvp>");
				}
			} else {
				commandSender.sendMessage("§aUse: §f/dano <global/pvp>");
			}

			return false;
		}

		@EventHandler
		public void entityHit(EntityDamageEvent event) {
			if (!ServerOptions.GLOBAL_PVP.isActive()) {
				event.setCancelled(true);
			}
		}

		@EventHandler
		public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
			if (!ServerOptions.PVP.isActive()) {
				event.setCancelled(true);
			}
		}

	}

	public static class TstCommand extends BukkitCommand implements Listener {

		public TstCommand() {
			super("tst", "Change the option of damage in the server.");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {

			if (!hasPermission(commandSender, "dano")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			String playerName = args[0];
			UUID uuid = isUUID(playerName) ? UUID.fromString(playerName)
					: getCoreManager().getNameFetcher().getUUID(playerName);

			if (uuid == null) {
				sendMessage(commandSender, "§cUsuário inexistente.");
			}
			
			Account accountPlayer = new Account(uuid);

			if (!accountPlayer.isLoaded()) {
				accountPlayer.load(new Callback<Boolean>() {
					public void finish(Boolean bool) {
						if (bool)
							getCoreManager().getAccountManager().getAccounts().put(uuid, accountPlayer);
						else {
							try {
								Thread.sleep(500L);
							} catch (InterruptedException exception) {
								exception.printStackTrace();
							}
						}
					}
				});
				accountPlayer.updatePlayer(args[0]);
			}
			System.out.println("-------------------------------------");
			System.out.println("" + playerName);
			System.out.println("" + accountPlayer.getDataHandler().getValue(DataType.CUP_GROUP).getValue());
			System.out.println("-------------------------------------");

			return false;
		}

		@EventHandler
		public void entityHit(EntityDamageEvent event) {
			if (!ServerOptions.GLOBAL_PVP.isActive()) {
				event.setCancelled(true);
			}
		}

		@EventHandler
		public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
			if (!ServerOptions.PVP.isActive()) {
				event.setCancelled(true);
			}
		}

	}

	public static class BuildCommand extends BukkitCommand implements Listener {

		public BuildCommand() {
			super("build", "Change the option of build in the server.");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!hasPermission(commandSender, "build")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			ServerOptions.BUILD.setActive(!ServerOptions.BUILD.isActive());
			Bukkit.broadcastMessage("§7Construção de blocos: "
					+ (ServerOptions.BUILD.isActive() ? "§aATIVADO".toUpperCase() : "§cDESATIVADO".toUpperCase()));

			return true;
		}

		@EventHandler
		public void onPlace(BlockPlaceEvent event) {
			if (!ServerOptions.BUILD.isActive()) {
				if (!hasPermission(event.getPlayer(), "build")) {
					event.setCancelled(true);
				}
			}
		}

		@EventHandler()
		public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
			Material bucket = event.getBucket();
			if (bucket.toString().contains("LAVA")) {
				if (!ServerOptions.BUILD.isActive()) {
					if (!hasPermission(event.getPlayer(), "build")) {
						event.setCancelled(true);
					}
				}
			}

			if (bucket.toString().contains("WATER")) {
				if (!ServerOptions.BUILD.isActive()) {
					if (!hasPermission(event.getPlayer(), "build")) {
						event.setCancelled(true);
					}
				}
			}
		}

		@EventHandler
		public void onBreak(BlockBreakEvent event) {
			if (!ServerOptions.BUILD.isActive()) {
				if (!hasPermission(event.getPlayer(), "build")) {
					event.setCancelled(true);
				}
			}
		}
	}

	public static class BlacklistCommand extends BukkitCommand {

		public BlacklistCommand() {
			super("blacklist");
		}

		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!hasPermission(commandSender, "event")) {
				return false;
			}

			if (args.length < 1) {
				commandSender.sendMessage("§0§lBLACKLIST §fUse: /blacklist <player/uniqueid>");
			} else {
				Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
						new AsyncBanTask(commandSender, args));
			}
			return false;
		}

		private final class AsyncBanTask implements Runnable {

			private final CommandSender commandSender;
			private final String[] args;

			private AsyncBanTask(CommandSender commandSender, String[] args) {
				this.commandSender = commandSender;
				this.args = args;
			}

			public void run() {

				if (getCoreManager().getPunishManager().canPunish(args[0])) {

					int id = (isUUID(args[0]) ? getCoreManager().getNameFetcher().getId(UUID.fromString(args[0]))
							: getCoreManager().getNameFetcher().getId(args[0]));

					String motive = "Blacklist de eventos";

					if (id == -1) {
						commandSender.sendMessage("§0§lBLACKLIST §fO player nunca entrou no servidor.");
						return;
					}

					if (Bukkit.getPlayer(args[0]) == null) {
						commandSender.sendMessage("§0§lBLACKLIST §fÉ necessário que o player esteja online.");
						return;
					}

					PunishRecord record = getCoreManager().getPunishManager().punishPlayer(id, 1, motive,
							System.currentTimeMillis() + 1, PunishType.TEMPBAN);

					if (record != null) {
						commandSender.sendMessage("§0§lBLACKLIST §fVocê §a§lADICIONOU§f o player §7" + args[0]
								+ "§f na BlackList de eventos!");

						Bukkit.getPlayer(args[0]).kickPlayer("Você agora está na Blacklist de eventos.");
					} else {
						commandSender.sendMessage(
								"§0§lBLACKLIST §fErro ao tentar efetuar a operação, tente novamente mais tarde.");
						return;
					}

				} else {
					commandSender.sendMessage(
							"§0§lBLACKLIST §fErro ao tentar efetuar a operação, tente novamente mais tarde.");
				}
			}
		}
	}

}

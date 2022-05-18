package br.com.zenix.core.spigot.commands.worldedit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.bo2.BO2Constructor.FutureBlock;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.item.ItemBuilder;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class WorldEditCommands extends BukkitCommand {

	public WorldEditCommands() {
		super("/pos1", "Select the first location.");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		if (!hasPermission(commandSender, "event")) {
			return false;
		}

		if (args.length != 0) {
			commandSender.sendMessage("§aUse: §f/pos1");
			return false;
		}

		Player player = (Player) commandSender;
		Location location = player.getLocation();
		getCoreManager().getWorldEditManager().setFirstPosition(player.getUniqueId(), location);
		player.sendMessage("§aA primeira localização foi setada §f(" + location.getBlockX() + "," + location.getBlockY()
				+ "," + location.getBlockZ() + ").");
		return true;
	}

	public static class PosTwoCommand extends BukkitCommand {

		public PosTwoCommand() {
			super("/pos2", "Select the second location.");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!hasPermission(commandSender, "event")) {
				return false;
			}

			if (args.length != 0) {
				commandSender.sendMessage("§aUse: §f/pos2");
				return false;
			}

			Player player = (Player) commandSender;
			Location location = player.getLocation();
			getCoreManager().getWorldEditManager().setSecondPosition(player.getUniqueId(), location);
			player.sendMessage("§aA segunda localização foi setada §f(" + location.getBlockX() + ","
					+ location.getBlockY() + "," + location.getBlockZ() + ").");
			return true;
		}
	}

	public static class WandCommand extends BukkitCommand {

		public WandCommand() {
			super("/wand", "Take a wand to modify the world.");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!hasPermission(commandSender, "event")) {
				return false;
			}

			if (args.length != 0) {
				commandSender.sendMessage("§aUse: §f/wand");
				return false;
			}

			Player player = (Player) commandSender;
			player.getInventory().addItem(new ItemBuilder(Material.BLAZE_ROD).setName("§aWand").getStack());
			player.sendMessage("§aVocê ativou o modo de construção!");
			return true;
		}
	}

	public static class UndoCommand extends BukkitCommand {

		public UndoCommand() {
			super("/undo", "Undo your shit.");
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!hasPermission(commandSender, "event")) {
				return false;
			}

			if (args.length != 0) {
				commandSender.sendMessage("§aUse: §f/undo");
				return false;
			}

			Player player = (Player) commandSender;

			if (!getCoreManager().getWorldEditManager().hasUndoPosition(player.getUniqueId())) {
				commandSender.sendMessage("§cVocê não pode desfazer algo que você não fez.");
				return false;
			}

			Location firstLocation = getCoreManager().getWorldEditManager().getUndoLocation(player.getUniqueId())[0];
			Location secondLocation = getCoreManager().getWorldEditManager().getUndoLocation(player.getUniqueId())[1];

			for (Location location : getCoreManager().getWorldEditManager().getLocationsFromTwoPoints(firstLocation,
					secondLocation)) {
				FutureBlock real = getCoreManager().getWorldEditManager().getCacheBlock(location);
				getCoreManager().getBO2().setBlockFast(location, Material.getMaterial(real.getId()), real.getData());
				getCoreManager().getWorldEditManager().removeCacheBlock(location);
			}
			commandSender.sendMessage("§aVocê desfez o set das localizações §f(" + firstLocation.getBlockX() + ","
					+ firstLocation.getBlockY() + "," + firstLocation.getBlockZ() + ")§a á §f("
					+ secondLocation.getBlockX() + "," + secondLocation.getBlockY() + "," + secondLocation.getBlockZ()
					+ ")§a");
			return true;
		}
	}

	public static class SetCommand extends BukkitCommand {

		public SetCommand() {
			super("/set", "Fill blocks in determinated locations.");
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!hasPermission(commandSender, "event")) {
				return false;
			}

			if (args.length != 1) {
				commandSender.sendMessage("§aUse: /set <idblock>");
				return false;
			}

			if (!isInteger(args[0])) {
				commandSender.sendMessage("§cO bloco precisa ser um número (id)!");
				return false;
			}

			Player player = (Player) commandSender;
			if (!getCoreManager().getWorldEditManager().hasFirstPosition(player.getUniqueId())) {
				commandSender.sendMessage("§cA primeira localização não foi setada.");
				return false;
			}

			if (!getCoreManager().getWorldEditManager().hasSecondPosition(player.getUniqueId())) {
				commandSender.sendMessage("§cA segunda localização não foi setada.");
				return false;
			}

			Integer blockId = Integer.valueOf(args[0]);
			if (Material.getMaterial(blockId) == null) {
				commandSender.sendMessage("§cO bloco com o id §f" + blockId + "§a não foi encontrado!");
				return false;
			}

			Location firstLocation = getCoreManager().getWorldEditManager().getFistPosition(player.getUniqueId());
			Location secondLocation = getCoreManager().getWorldEditManager().getSecondPosition(player.getUniqueId());
			Material blockMaterial = Material.getMaterial(blockId);

			getCoreManager().getWorldEditManager().setUndoPosition(player.getUniqueId(),
					new Location[] { firstLocation, secondLocation });

			for (Block block : getCoreManager().getWorldEditManager().getblocksFromTwoPoints(firstLocation,
					secondLocation)) {

				getCoreManager().getWorldEditManager().setCacheBlock(block.getLocation(), block);
				getCoreManager().getBO2().setBlockFast(block.getLocation(), blockMaterial, (byte) 0);
			}

			commandSender.sendMessage("§aVocê setou o bloco §f" + blockMaterial.name().toLowerCase() + "§a de §f("
					+ firstLocation.getBlockX() + "," + firstLocation.getBlockY() + "," + firstLocation.getBlockZ()
					+ ")§a á §f(" + secondLocation.getBlockX() + ","
					+ secondLocation.getBlockY() + "," + secondLocation.getBlockZ() + ") " + getCoreManager()
							.getWorldEditManager().getblocksFromTwoPoints(firstLocation, secondLocation).size()
					+ " §ablocos.");

			return true;
		}

	}
}

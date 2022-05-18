package br.com.zenix.core.spigot.commands.moderators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.options.ServerOptions;
import br.com.zenix.core.spigot.player.events.PlayerInventoryOpenEvent;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class ModerateCommands extends BukkitCommand {

	public ModerateCommands() {
		super("teleport", "", Arrays.asList("tp"));
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!isPlayer(commandSender)) {
			return false;
		}

		if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
			sendPermissionMessage(commandSender);
			return false;
		}

		Player player = (Player) commandSender;
		if (args.length == 1) {
			if (Bukkit.getPlayer(args[0]) != null) {
				Player player2 = Bukkit.getPlayer(args[0]);
				player.teleport(player2);
				if (getCoreManager().getSkinManager().usingFake(player.getUniqueId())) {
					sendWarning(player.getName() + "("
							+ getCoreManager().getSkinManager().getInFake().getValue(player.getUniqueId())
							+ ") teleportou-se para " + player2.getName());
				} else {
					sendWarning(player.getName() + " teleportou-se para " + player2.getName());
				}

				player.sendMessage("§aVocê foi teleportado para o player §f" + player2.getName());
			} else {
				sendOfflinePlayerMessage(commandSender, args[0]);
				return false;
			}
		} else if (args.length == 2) {
			if (Bukkit.getPlayer(args[0]) != null) {
				if (Bukkit.getPlayer(args[1]) != null) {
					Player player1 = Bukkit.getPlayer(args[0]);
					Player player2 = Bukkit.getPlayer(args[1]);
					player1.teleport(player2);

					if (getCoreManager().getSkinManager().usingFake(player.getUniqueId())) {
						sendWarning(player.getName() + "("
								+ getCoreManager().getSkinManager().getInFake().getValue(player.getUniqueId())
								+ ") teleportou " + player1.getName() + " para " + player2.getName());
					} else {
						sendWarning(
								player.getName() + " teleportou " + player1.getName() + " para " + player2.getName());
					}

					player.sendMessage("§aVocê teleportou o player §f" + player1.getName() + "§a para o player §f"
							+ player2.getName());
				} else {
					sendOfflinePlayerMessage(commandSender, args[0]);
					return false;
				}
			} else {
				sendOfflinePlayerMessage(commandSender, args[0]);
				return false;
			}
		} else if (args.length == 3) {
			if (!isInteger(args[0]) && !isInteger(args[1]) && !isInteger(args[2])) {
				commandSender.sendMessage("§aUse: §f/tp <x> <y> <z>");
				return false;
			}

			Location loc = new Location(player.getWorld(), getInteger(args[0]), getInteger(args[1]),
					getInteger(args[2]));

			loc.getWorld().refreshChunk(loc.getChunk().getX(), loc.getChunk().getZ());
			player.teleport(loc);

			if (getCoreManager().getSkinManager().usingFake(player.getUniqueId())) {
				sendWarning(player.getName() + "("
						+ getCoreManager().getSkinManager().getInFake().getValue(player.getUniqueId())
						+ ") teleportou-se para as coordenadas " + getInteger(args[0]) + ", " + getInteger(args[1])
						+ ", " + getInteger(args[2]));
			} else {
				sendWarning(player.getName() + " teleportou-se para as coordenadas " + getInteger(args[0]) + ", "
						+ getInteger(args[1]) + ", " + getInteger(args[2]));
			}

			sendWarning(player.getName() + " teleportou-se para as coordenadas " + getInteger(args[0]) + ", "
					+ getInteger(args[1]) + ", " + getInteger(args[2]));
			player.sendMessage("§fVocê teleportou para §fX:" + args[0] + "§f, Y:" + args[1] + ", Z:" + args[2]);

		} else {
			commandSender.sendMessage("§aUse: §f/tp <player>");
			return false;
		}

		return true;
	}

	public static class GlobalTeleportCommand extends BukkitCommand {

		public GlobalTeleportCommand() {
			super("tpall", "");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.event")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			Player player = (Player) commandSender;
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.teleport(player);
				players.sendMessage("§aVocê foi teleportado por algum staffer.");
			}

			player.sendMessage(
					"§aForam teletransportados §f" + Bukkit.getOnlinePlayersSize() + "§a players para você!");

			return true;
		}

	}

	public static class GamemodeCommand extends BukkitCommand {

		public GamemodeCommand() {
			super("gamemode", "Change your gamemode.", Arrays.asList("gm"));
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			if (args.length == 0) {
				commandSender.sendMessage("§aUse: §f/gamemode <mode> <player>");
			} else if (args.length == 1) {
				String mode = args[0];
				Player player = (Player) commandSender;

				GameMode gamemode = getGamemode(mode);

				if (gamemode == null) {
					player.sendMessage("§cO modo selecionado é inválido.");
					return false;
				}

				if (player.getGameMode() == gamemode) {
					player.sendMessage("§cVocê já está no §c" + player.getGameMode().name());
				} else {
					player.setGameMode(gamemode);
					player.sendMessage("§aVocê alterou seu gamemode para §a" + player.getGameMode().name());
					sendWarning("O staffer " + player.getName() + " trocou o modo de jogo para "
							+ gamemode.name().toLowerCase() + ".");
				}
				return true;
			} else if (args.length == 2) {
				String mode = args[0];
				Player player = (Player) commandSender;
				Player target = Bukkit.getPlayer(args[1]);

				if (target == null) {
					player.sendMessage("§cUsuário offline.");
					return true;
				}

				GameMode gamemode = getGamemode(mode);

				if (gamemode == null) {
					player.sendMessage("§cModo inválido.");
					return false;
				}

				target.setGameMode(gamemode);
				player.sendMessage("§aVocê alterou o gamemode do player §f" + target.getName() + " §a para §f"
						+ target.getGameMode());
				sendWarning("O staffer " + player.getName() + " alterou o modo de jogo " + gamemode.name().toLowerCase()
						+ " para o player " + target.getName() + ".");
			} else {
				commandSender.sendMessage("§3§lGAMEMODE §fUse: /gamemode <mode> <player>");
			}
			return false;
		}

		public GameMode getGamemode(String mode) {
			GameMode gamemode = null;
			if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("survival")) {
				gamemode = GameMode.SURVIVAL;
			} else if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("criativo")) {
				gamemode = GameMode.CREATIVE;
			} else if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("aventura")) {
				gamemode = GameMode.CREATIVE;
			}
			return gamemode;
		}

	}

	public static class ClearChatCommand extends BukkitCommand {

		public ClearChatCommand() {
			super("cc", "");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			Player player = (Player) commandSender;

			for (int i = 0; i < 200; i++) {
				Bukkit.broadcastMessage(" ");
			}

			player.sendMessage("§aVocê §flimpou§a o chat!");
			return true;
		}

	}

	public static class ChatCommand extends BukkitCommand {

		public ChatCommand() {
			super("chat", "");
		}

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
				sendPermissionMessage(commandSender);
				return false;
			}
			if (args.length == 0) {
				showHelp(commandSender);
			} else {
				if (args[0].equalsIgnoreCase("clear")) {
					for (int i = 0; i < 200; i++) {
						Bukkit.broadcastMessage(" ");
					}
					commandSender.sendMessage("§aVocê limpou o chat!");
				} else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {

					ServerOptions.CHAT.setActive(!ServerOptions.CHAT.isActive());
					commandSender.sendMessage("§aVocê " + (ServerOptions.CHAT.isActive() ? "§fhabilitou".toUpperCase()
							: "§fdesabilitou".toUpperCase()) + "§a o chat!");
					Bukkit.broadcastMessage("§cO chat foi " + (ServerOptions.CHAT.isActive()
							? "§fHABILITADO".toUpperCase() : "§fdesabilitado".toUpperCase()) + "§f!");

				} else {
					showHelp(commandSender);
					return false;
				}
			}
			return true;

		}

		public void showHelp(CommandSender commandSender) {
			commandSender.sendMessage("§aUse: §f/chat <clear," + (ServerOptions.CHAT.isActive() ? "off" : "on") + ">");
		}

	}

	public static class InvseeCommand extends BukkitCommand implements Listener {

		public InvseeCommand() {
			super("invsee", "");
		}

		public static final HashMap<UUID, Inventory> playerInventory = new HashMap<UUID, Inventory>();

		@Override
		public boolean execute(CommandSender commandSender, String label, String[] args) {
			if (!isPlayer(commandSender)) {
				return false;
			}

			if (!((Player) commandSender).hasPermission("commons.cmd.moderate")) {
				sendPermissionMessage(commandSender);
				return false;
			}

			Player player = (Player) commandSender;
			if (args.length != 1) {
				commandSender.sendMessage("§aUse: §f/invsee <player>");
				return false;
			}

			if (playerInventory.containsKey(player.getUniqueId()))
				return false;

			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sendOfflinePlayerMessage(commandSender, args[0]);
				return false;
			}

			PlayerInventoryOpenEvent event = new PlayerInventoryOpenEvent(player, target.getInventory());

			Bukkit.getPluginManager().callEvent(event);

			if (!event.isCancelled()) {

				playerInventory.put(player.getUniqueId(), target.getInventory());
				player.openInventory(target.getInventory());
				player.sendMessage("§aVisualizando o inventario de: §f" + target.getName());
			} else {
				player.sendMessage("§aVisualizando o inventario de: §f" + target.getName());
			}
			return true;
		}

		@EventHandler
		private void onClose(InventoryCloseEvent event) {
			if (playerInventory.containsKey(event.getPlayer().getUniqueId())) {
				playerInventory.remove(event.getPlayer().getUniqueId());
			}
		}
	}

}

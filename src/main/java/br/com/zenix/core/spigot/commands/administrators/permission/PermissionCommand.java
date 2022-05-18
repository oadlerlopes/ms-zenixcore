package br.com.zenix.core.spigot.commands.administrators.permission;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.networking.packet.direction.out.PacketOutPlayerPermission;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.permissions.Permissible;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PermissionCommand extends BukkitCommand {

	public PermissionCommand() {
		super("permission", "Modify player permissions.");
	}

	public boolean execute(CommandSender commandSender, String label, String[] args) {
		if (!hasPermission(commandSender, "permission")) {
			return false;
		}

		if (isPlayer(commandSender))
			return false;

		if (args.length == 0) {
			sendHelp(commandSender);
		} else if (args.length == 1) {
			sendHelp(commandSender);
		} else if (args.length == 2) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncPermissionListTask(commandSender, args));
		} else if (args.length == 3) {
			sendHelp(commandSender);
		} else if (args.length == 4) {
			Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncPermissionSetTask(commandSender, args));
		} else {
			sendHelp(commandSender);
		}

		return false;
	}

	public boolean validString(String str) {
		return (str.matches("[a-zA-Z0-9_]+")) && str.length() >= 2 && str.length() <= 6;
	}

	private void sendHelp(CommandSender commandSender) {
		sendMessage(commandSender, "§aUse: §f/permission add/remove <nick/uuid> (permission> (time> §f- §7adicione/remova uma permissão de um player.");
		sendMessage(commandSender, "§aUse: §f/permission list <nick/uuid> §f- §7veja as permissões de um player.");
	}

	private BaseComponent buildComponent(Permissible permissible) {
		BaseComponent baseComponent = new TextComponent(permissible.getName() + (permissible.isActive() ? "§6" : "§c"));
		BaseComponent permissions = new TextComponent("§fExpira em: " + (getCoreManager().getUtils().compareSimpleTime(System.currentTimeMillis(), permissible.getTime())));
		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { permissions }));
		return baseComponent;
	}

	private final class AsyncPermissionListTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncPermissionListTask(CommandSender sender, String[] args) {
			this.commandSender = sender;
			this.args = args;
		}

		public void run() {

			if (args[0].equalsIgnoreCase("list")) {
				String name = args[1];

				UUID uuid = isUUID(name) ? UUID.fromString(name) : getCoreManager().getNameFetcher().getUUID(name);
				if (uuid == null) {
					sendMessage(commandSender, "§cUsuário inexistente.");
					return;
				}

				int id = getCoreManager().getNameFetcher().getId(uuid);

				ArrayList<Permissible> permissions = getCoreManager().getPermissionManager().getPlayerPermissions(id);

				if (isPlayer(commandSender)) {

					Player player = (Player) commandSender;

					TextComponent tagsMessage = new TextComponent("§aPermissões do player §f" + args[1]);
					for (int i = 0; i < permissions.size(); i++) {
						Permissible permissible = permissions.get(i);
						tagsMessage.addExtra(i == 0 ? "" : ", ");
						tagsMessage.addExtra(buildComponent(permissible));
					}

					player.spigot().sendMessage(tagsMessage);
				} else {

					String perm = "";

					sendMessage(commandSender, "§fPermissões do jogador: §a" + args[1]);
					for (int i = 0; i < permissions.size(); i++) {
						Permissible permissible = permissions.get(i);
						perm += (i == 0 ? "" : ", ");
						perm += (permissible.isActive() ? "§6" : "§c") + permissible.getName();
					}
					sendMessage(commandSender, perm);

				}

			} else {
				sendHelp(commandSender);
			}
		}
	}

	private final class AsyncPermissionSetTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncPermissionSetTask(CommandSender sender, String[] args) {
			this.commandSender = sender;
			this.args = args;
		}

		public void run() {

			String argument = args[0];
			if (argument.equalsIgnoreCase("add") || argument.equalsIgnoreCase("remove")) {

				String name = args[1];

				UUID uuid = isUUID(name) ? UUID.fromString(name) : getCoreManager().getNameFetcher().getUUID(name);
				if (uuid == null) {
					sendMessage(commandSender, "§cUsuário offline!");
					return;
				}

				long time;
				try {
					time = getCoreManager().getUtils().parseDateDiff(args[3], true);
				} catch (Exception e) {
					sendMessage(commandSender, "§cErro em formatar o tempo §f" + args[3]);
					return;
				}

				int id = getCoreManager().getNameFetcher().getId(uuid);

				String permission = args[2];
				boolean add = argument.equalsIgnoreCase("add") ? true : false;

				if (getCoreManager().getPermissionManager().updatePermissionsPlayer(id, permission, add, time)) {
					sendMessage(commandSender, "§aVocê §f" + (add ? "adicionou" : "removeu") + "§a a permissão §f" + permission + " " + (add ? "para o" : "do") + "§a player §f" + name + ".");
				} else {
					sendMessage(commandSender, "§cErro ao modificar a permissão " + permission + " do player " + name + ", favor verificar erro no console.");
				}

				Bukkit.getScheduler().runTask(getCoreManager().getPlugin(), new PostPermissionSetTask(uuid, permission, add));

			} else {
				sendHelp(commandSender);
			}
		}
	}

	private final class PostPermissionSetTask implements Runnable {

		private final UUID uuid;
		private final String permission;
		private final boolean value;

		private PostPermissionSetTask(UUID uuid, String permission, boolean value) {
			this.uuid = uuid;
			this.permission = permission;
			this.value = value;
		}

		public void run() {
			Account player = getCoreManager().getAccountManager().getAccount(uuid);
			if (player != null) {
				player.getPermissions().put(permission, value);
				player.setRank(player.getRank(), player.getRankTime());
			} else {
				getCoreManager().getPacketHandler().sendGlobalPacket(new PacketOutPlayerPermission(uuid, permission, value));
			}
		}
	}

}

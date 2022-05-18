package br.com.zenix.core.spigot.commands.administrators.permission;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.zenix.core.networking.packet.direction.out.PacketOutRankHandler;
import br.com.zenix.core.networking.packet.direction.out.PacketOutRankUpdate;
import br.com.zenix.core.networking.packet.direction.out.PacketOutSetPermission;
import br.com.zenix.core.spigot.commands.base.BukkitCommand;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.permissions.PermissionManager;
import br.com.zenix.core.spigot.player.permissions.constructor.Rank;
import br.com.zenix.core.spigot.player.tag.constructor.Tag;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class GroupCommand extends BukkitCommand {

	public GroupCommand() {
		super("group");
	}

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {

		if (isPlayer(commandSender)) {
			if (!hasPermission(commandSender, "group")) {
				return false;
			}
		}

		Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new AsyncGroupTask(commandSender, args));

		return false;

	}

	private void sendHelp(CommandSender commandSender) {
		commandSender.sendMessage("§d§l> §fUse um dos comandos abaixo!");
		commandSender.sendMessage("§d§l> §f/group (group) add/remove (permission) §f- §aadicione uma permissão para um grupo.");
		commandSender.sendMessage("§d§l> §f/group (group) copy (name) §f- §acopie um grupo.");
		commandSender.sendMessage("§d§l> §f/group (group) create §f- §acrie um grupo.");
		commandSender.sendMessage("§d§l> §f/group (group) delete §f- §adelete um grupo.");
		commandSender.sendMessage("§d§l> §f/group (group) default §f- §adefina um grupo como default.");
		commandSender.sendMessage("§d§l> §f/group (group) tag (tag/tagID) §f- §adefina a tag de um grupo.");
		commandSender.sendMessage("§d§l> §f/group (group) info §f- §aveja informação de um grupo.");
		commandSender.sendMessage("§d§l> §f/group list §f- §aveja os grupos do servidor.");
	}

	public boolean validString(String str) {
		return (str.matches("[a-zA-Z_]+")) && (!str.contains(".com") && str.length() >= 3 && str.length() < 17);
	}

	private BaseComponent buildGroupComponent(Rank rank) {
		BaseComponent baseComponent = new TextComponent(rank.getName());
		BaseComponent permissions = new TextComponent("§fPermissões:");

		for (String perm : rank.getPermissions().keySet())
			permissions.addExtra("\n" + (rank.getPermissions().get(perm) ? "§a" : "§c") + perm + ",");

		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { permissions }));
		return baseComponent;
	}

	private final class AsyncGroupTask implements Runnable {

		private final CommandSender commandSender;
		private final String[] args;

		private AsyncGroupTask(CommandSender commandSender, String[] args) {
			this.commandSender = commandSender;
			this.args = args;
		}

		@SuppressWarnings("static-access")
		public void run() {

			if (args.length == 0) {
				sendHelp(commandSender);
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {

					PermissionManager permissionManager = getCoreManager().getPermissionManager();

					if (!(commandSender instanceof Player)) {
						String groups = "§aMostrando (" + permissionManager.getRanks().size() + ") grupos existentes: ";
						for (Rank rank : permissionManager.getRanks().values())
							groups += (groups.length() == 0 ? "" : ", ") + rank.getName();
						commandSender.sendMessage(groups);
						return;
					}

					Player player = (Player) commandSender;

					List<Rank> groups = new LinkedList<>(permissionManager.getRanks().values());

					TextComponent groupMessages = new TextComponent("§d§l> §fMostrando (" + permissionManager.getRanks().size() + ") grupos existentes: ");
					for (int i = 0; i < groups.size(); i++) {
						Rank rank = groups.get(i);
						groupMessages.addExtra(i == 0 ? "" : ", ");
						groupMessages.addExtra(buildGroupComponent(rank));
					}
					player.spigot().sendMessage(groupMessages);

				} else {
					sendHelp(commandSender);
				}
			} else if (args.length == 2) {
				String groupName = args[0];
				String argument = args[1];

				PermissionManager permissionManager = getCoreManager().getPermissionManager();

				Rank rank = permissionManager.getRank(groupName);

				if (argument.equalsIgnoreCase("create")) {
					if (rank != null) {
						commandSender.sendMessage("§d§l> §fO grupo §c" + groupName + "§f já existe.");
						return;
					}

					if (isInteger(groupName)) {
						commandSender.sendMessage("§d§l> §fO nome do grupo não pode ser composto por números.");
						return;
					}

					if (!validString(groupName)) {
						commandSender.sendMessage("§d§l> §fO nome do grupo não pode ser validado.");
						return;
					}

					int groupId = permissionManager.createGroup(groupName);
					if (groupId != -1) {
						commandSender.sendMessage("§aVocê criou o grupo " + groupName + "(" + groupId + ") corretamente, favor definir a tag deste grupo.");
						Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(),
								new PostGroupHandlerTask(new Rank(groupName, groupId, getCoreManager().getTagManager().getTags().values().iterator().next(), 0, false), true));
					} else {
						commandSender.sendMessage("§d§l> §fErro ao criar o grupo " + groupName + " favor verificar erro no console.");
					}
				}

				if (rank == null) {
					commandSender.sendMessage("§d§l> §fO grupo §f" + groupName + "§f não existe.");
					return;
				}

				if (argument.equalsIgnoreCase("delete")) {

					if (rank.isDefaultRank()) {
						commandSender.sendMessage("§d§l> §fO grupo a ser deletado não pode ser um grupo default.");
						return;
					}

					if (permissionManager.deleteGroup(rank)) {
						commandSender.sendMessage("§d§l> §fVocê deletou o grupo " + groupName + " corretamente.");
						Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new PostGroupHandlerTask(rank, false));
					} else {
						commandSender.sendMessage("§d§l> §fErro ao deletar o grupo " + groupName + " favor verificar erro no console.");
					}
				} else if (argument.equalsIgnoreCase("default")) {
					if (permissionManager.defaultGroup(rank)) {
						commandSender.sendMessage("§aVocê colocou o grupo " + groupName + " como default.");
						Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new PostGroupUpdateTask(rank, rank.getTag(), true));
					} else {
						commandSender.sendMessage("§cErro ao colocar o grupo " + groupName + " como default favor verificar erro no console.");
					}
				} else if (argument.equalsIgnoreCase("info")) {
					commandSender.sendMessage("§fInformações sobre o grupo §a" + groupName + "§f:");
					commandSender.sendMessage("§fDefault: " + (rank.isDefaultRank() ? "§aSim" : "§cNão"));

					String permissions = "";

					for (String perm : rank.getPermissions().keySet())
						permissions += (rank.getPermissions().get(perm) ? "§a" : "§c") + perm + ",";

					commandSender.sendMessage("§fPermissões: " + permissions.substring(0, permissions.length() - 1));
					return;
				} else {
					sendHelp(commandSender);
				}
			} else if (args.length == 3) {
				String groupName = args[0];
				String argument = args[1];

				PermissionManager permissionManager = getCoreManager().getPermissionManager();

				Rank rank = permissionManager.getRank(groupName);

				if (rank == null) {
					commandSender.sendMessage("§d§l> §fO grupo §e" + groupName + "§f não existe.");
					return;
				}

				if (argument.equalsIgnoreCase("add") || argument.equalsIgnoreCase("remove")) {
					String permission = args[2];
					boolean add = argument.equalsIgnoreCase("add") ? true : false;

					boolean exists = false;

					if (rank.getPermissions().keySet().contains(permission.toLowerCase()) && rank.getPermissions().get(permission.toLowerCase()) == add) {
						exists = true;
					}

					if (add && exists) {
						commandSender.sendMessage("§d§l> §fA permissão §e" + permission.toLowerCase() + "§f já está presente neste grupo.");
						return;
					}

					if (permissionManager.updatePermissionGroup(permission, add, rank)) {
						commandSender.sendMessage("§d§l> §fVocê " + (add ? "adicionou" : "removeu") + " a permissão " + permission + " " + (add ? "no" : "do") + " grupo " + groupName + ".");
						Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new PostUpdatePermissionTask(rank, permission, add));
					} else {
						commandSender.sendMessage("§cErro ao modificar a permissão " + permission + " do grupo " + groupName + ", favor verificar erro no console.");
					}
				} else if (argument.equalsIgnoreCase("tag")) {
					String tagName = args[2];

					Tag tag = getCoreManager().getTagManager().getTag(tagName);
					if (tag == null) {
						commandSender.sendMessage("§d§l> §fA tag §f" + tagName + "§c não existe.");
						return;
					}
				} else if (argument.equalsIgnoreCase("copy")) {
					String name = args[2];

					int groupId = permissionManager.copyGroup(rank, name);
					if (groupId != -1) {
						commandSender.sendMessage("§d§l> §fVocê copiou o grupo " + groupName + "(" + groupId + ") corretamente, favor definir a tag deste grupo.");
						return;
					} else {
						commandSender.sendMessage("§d§l> §fErro ao copiar o grupo " + groupName + " favor verificar erro no console.");
						return;
					}
				} else {
					sendHelp(commandSender);
				}
			} else {
				sendHelp(commandSender);
			}
		}
	}

	private final class PostUpdatePermissionTask implements Runnable {

		private final Rank rank;
		private final String permission;
		private final boolean value;

		private PostUpdatePermissionTask(Rank rank, String permission, boolean value) {
			this.rank = rank;
			this.permission = permission;
			this.value = value;
		}

		@Override
		public void run() {

			rank.addPermission(permission, value);
			for (Account account : getCoreManager().getAccountManager().getAccounts().values()) {
				if (account.getRank().getId() == rank.getId()) {
					account.setRank(rank, account.getRankTime());
				}
			}

			getCoreManager().getPacketHandler().sendGlobalPacket(new PacketOutSetPermission(rank, permission, value));

		}
	}

	private final class PostGroupUpdateTask implements Runnable {

		private final Rank rank;
		private final Tag tag;
		private final boolean value;

		private PostGroupUpdateTask(Rank rank, Tag tag, boolean value) {
			this.rank = rank;
			this.tag = tag == null ? rank.getTag() : tag;
			this.value = value;
		}

		@Override
		public void run() {

			rank.setTag(tag);
			rank.setDefaultRank(value);

			getCoreManager().getPacketHandler().sendGlobalPacket(new PacketOutRankUpdate(rank, tag, value));

		}
	}

	private final class PostGroupHandlerTask implements Runnable {

		private final Rank rank;
		private final boolean value;

		private PostGroupHandlerTask(Rank rank, boolean value) {
			this.rank = rank;
			this.value = value;
		}

		@Override
		public void run() {
			getCoreManager().getPacketHandler().sendGlobalPacket(new PacketOutRankHandler(rank, value));
		}
	}

}

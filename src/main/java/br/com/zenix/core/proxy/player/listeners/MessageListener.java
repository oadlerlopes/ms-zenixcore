package br.com.zenix.core.proxy.player.listeners;

import java.util.ArrayList;
import java.util.UUID;

import br.com.zenix.core.proxy.commands.base.ProxyListener;
import br.com.zenix.core.proxy.player.account.ProxyAccount;
import br.com.zenix.core.spigot.player.tag.type.TagType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class MessageListener extends ProxyListener {

	public static final ArrayList<UUID> staffChat = new ArrayList<UUID>();

	public static void addStaffChat(UUID uuid) {
		staffChat.add(uuid);
	}

	public static void removeStaffChat(UUID uuid) {
		staffChat.remove(uuid);
	}

	public static boolean isStaffchat(UUID uuid) {
		if (staffChat.contains(uuid)) {
			return true;
		}
		return false;
	}

	public static final ArrayList<UUID> toggle = new ArrayList<UUID>();

	public static void addToggle(UUID uuid) {
		toggle.add(uuid);
	}

	public static void removeToggle(UUID uuid) {
		toggle.remove(uuid);
	}

	public static boolean isToggle(UUID uuid) {
		if (toggle.contains(uuid)) {
			return true;
		}
		return false;
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		if (event.isCommand())
			return;

		if (event.isCancelled())
			return;

		if (!(event.getSender() instanceof ProxiedPlayer))
			return;

		ProxiedPlayer player = ((ProxiedPlayer) event.getSender());
		
		if (player == null)
			return;

		if (isStaffchat(player.getUniqueId())) {
			if (!hasAccount(player)) {
				removeStaffChat(player.getUniqueId());
				return;
			}
			sendStaffMessage(player, event.getMessage());
			event.setCancelled(true);
		}
	}

	public boolean hasAccount(ProxiedPlayer account) {
		if (account.hasPermission("commons.cmd.bungeemod")) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public void sendStaffMessage(ProxiedPlayer proxiedPlayer, String message) {
		for (ProxiedPlayer player : getProxyManager().getPlugin().getProxy().getPlayers()) {
			if (!getProxyManager().getAccountManager().getAccount(player.getUniqueId()).isSilent()) {
				String tag = getRanking(proxiedPlayer);

				String format = tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "") + ChatColor.WHITE
						+ ": ";
				if (hasAccount(player)) {
					if (!isToggle(player.getUniqueId())) {
						player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "[STAFF] " + format + message);
						getProxyManager().getLogger().log("[STAFF] " + format + message);
					}
				}
			}
		}
	}

	public String getRanking(ProxiedPlayer player) {
		ProxyAccount account = getProxyManager().getAccountManager().getAccount(player.getUniqueId());
		return "" + getTag(account.getGroup().getId()).getPrefix() + "" + player.getName();
	}

	public TagType getTag(int id) {
		for (TagType tagType : TagType.values()) {
			if (tagType.getId() == id) {
				return tagType;
			}
		}
		return null;
	}

}

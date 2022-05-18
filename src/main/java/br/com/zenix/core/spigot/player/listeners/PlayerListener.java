package br.com.zenix.core.spigot.player.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import br.com.zenix.core.networking.packet.direction.out.PacketOutClanChat;
import br.com.zenix.core.spigot.commands.base.BukkitListener;
import br.com.zenix.core.spigot.options.ServerOptions;
import br.com.zenix.core.spigot.player.account.Account;
import br.com.zenix.core.spigot.player.events.PlayerChatCoreEvent;
import br.com.zenix.core.spigot.player.events.PlayerTellCoreEvent;
import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.core.spigot.player.punish.constructor.PunishRecord;
import br.com.zenix.core.spigot.player.punish.type.PunishType;
import br.com.zenix.core.spigot.server.Variables;
import br.com.zenix.core.spigot.server.type.ServerType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class PlayerListener extends BukkitListener {

	public static final HashMap<UUID, Integer> chatCooldown = new HashMap<>();

	public String[] chatBlocks = new String[] { ".br", ".net", ".us", ".host", ". c o m", ". com", ".gs", ". b r",
			".br", "server falido", "servidor falido", "mushmc", "servidor lagado" };
	public String[] tellBlocks = new String[] { "preto", "freekill", "fudido", "negro", "ddos", "net", "derrubar",
			"macaco", "gorila", "preto", "picolé de asfalto", "baiano", "nordestino", "gordo", "vesgo", "piche",
			"maguila", "vadia", "puta", "piranha", "vagabunda", "órfão" };
	public String[] commandBlocks = new String[] { "/bukkit:?", "/bukkit:ver", "/bukkit:version", "/bukkit:versions",
			"/bukkit:tellraw", "/ver", "/version", "/versions", "/tellraw", "/bukkit:me", "/me", "/whisper" };

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTell(PlayerTellCoreEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Player toTell = event.getTarget();
		Player player = event.getPlayer();

		if (!getCoreManager().getAccountManager().getAccount(toTell).isTell()) {
			return;
		}

		String message = event.getMessage();

		if (Variables.EVENT == true) {
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission("commons.cmd.staff")) {
					staff.sendMessage(
							"§7[TELL] Mensagem de " + player.getName() + " para " + toTell.getName() + ": " + message);
				}
			}
		}

		for (String s : tellBlocks) {
			if (message.toLowerCase().contains(s.toLowerCase())) {
				for (Player staff : Bukkit.getOnlinePlayers()) {
					if (staff.hasPermission("commons.cmd.staff")) {
						staff.sendMessage("§7[TELL] Mensagem de " + player.getName() + " para " + toTell.getName()
								+ ": " + message);
					}
				}
			}
		}

		if (!getCoreManager().getSkinManager().usingFake(toTell.getName())) {
			toTell.sendMessage("§7[Mensagem de §f" + player.getName() + "§7] §7" + message);
			player.sendMessage("§7[Mensagem para §f" + toTell.getName() + "§7] §7" + message);
		} else {
			toTell.sendMessage("§7[Mensagem de §f" + player.getName() + "§7] §7" + message);
			player.sendMessage(
					"§7[Mensagem para §f" + getCoreManager().getSkinManager().getInFake().getValue(player.getUniqueId())
							+ "§7] §7" + message);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		event.setMessage(event.getMessage().replace("%", "%%"));
		Player player = event.getPlayer();

		if (!ServerOptions.CHAT.isActive()) {
			if (!player.hasPermission("commons.chat.normal")) {
				player.sendMessage("§cO chat está desabilitado.");
				event.setCancelled(true);
				return;
			}
		}

		Account account = getCoreManager().getAccountManager().getAccount(player);

		if (account != null)
			if (!account.getPunishRecords().isEmpty())
				for (PunishRecord punish : account.getPunishRecords().values()) {
					if (!punish.isActive())
						continue;
					if (punish.getType().equals(PunishType.MUTE)) {
						player.sendMessage("§cVocê está mutado permanentemente. Adquira seu unmute em loja.zenix.cc");
						event.setCancelled(true);
						return;
					} else if (punish.getType().equals(PunishType.TEMPMUTE)) {
						player.sendMessage("§cVocê está mutado temporariamente. A punição expira em "
								+ getCoreManager().getUtils().compareTime(punish.getExpire()));
						event.setCancelled(true);
						return;
					}
				}

		if (chatCooldown.containsKey(player.getUniqueId())) {
			String seconds = chatCooldown.get(player.getUniqueId()) == 1 ? " segundo" : " segundos";
			player.sendMessage(
					"§cAguarde " + chatCooldown.get(player.getUniqueId()) + seconds + " para falar novamente.");
			event.setCancelled(true);
			return;
		}

		String message = event.getMessage().toLowerCase();

		for (String string : chatBlocks) {
			if (message.toLowerCase().contains(string.toLowerCase())) {
				if (!player.hasPermission("*")) {
					event.setCancelled(true);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							"mute " + player.getName() + " msg: [" + event.getMessage() + "]");
					return;
				}
			}
		}

		if (player.hasPermission("commons.chat.colorplus")) {
			event.setMessage(event.getMessage().replace("&", "§"));
		}

		if (player.hasPermission("commons.chat.color")) {
			event.setMessage("§f" + event.getMessage());
		} else {
			event.setMessage("§7" + event.getMessage());
		}

		if (account.isClanChat()) {
			if (account.isHaveClan()) {
				getCoreManager().getPacketHandler()
						.sendGlobalPacket(new PacketOutClanChat(player.getName() + ": §f" + event.getMessage(),
								getCoreManager().getClanAccountManager().getClanAccount(player).getClan().getName()));
				event.setCancelled(true);

				for (Player players : Bukkit.getOnlinePlayers()) {
					Account accounts = getCoreManager().getAccountManager().getAccount(players);

					if (accounts.isHaveClan()) {
						if (accounts.isClanChat()) {
							if (getCoreManager().getClanAccountManager().getClanAccount(players).getClan().getName()
									.equalsIgnoreCase(getCoreManager().getClanAccountManager().getClanAccount(player)
											.getClan().getName())) {
								players.sendMessage(
										"§7[CLAN-" + getCoreManager().getClanAccountManager().getClanAccount(players).getClan().getName() + "] §c" + player.getName() + ": §f" + event.getMessage());
							}
						}
					}
				}

				return;
			}
		}

		if (!player.hasPermission("commons.chat.cooldown")) {
			chatCooldown.put(player.getUniqueId(), 3);
		}

		String stringBuilder = "";

		if (getCoreManager().getServerType() == ServerType.PRACTICE) {
			stringBuilder += "" + getCoreManager().getTagManager().getDisplayName(player) + " "
					+ (getCoreManager().getSkinManager().usingFake(player.getUniqueId()) ? "" : "") + " §7» §f";
		} else {
			stringBuilder += (account.isHaveClan() ? "§8["
					+ getCoreManager().getClanAccountManager().getClanAccount(player).getClan().getTag().toUpperCase()
					+ "] " : "") + getCoreManager().getTagManager().getDisplayName(player) + " "
					+ (getCoreManager().getSkinManager().usingFake(player.getUniqueId()) ? "§7(§f-§7)"
							: getCoreManager().getAccountManager().getAccount(player).getLeaguePrefix())
					+ " §7» §f";
		}

		PlayerChatCoreEvent eventChat = new PlayerChatCoreEvent(player, event.getMessage(), stringBuilder.toString(),
				event.getRecipients());
		if (eventChat != null)
			Bukkit.getPluginManager().callEvent(eventChat);

		if (event.getRecipients() != null) {
			Iterator<Player> recipients = event.getRecipients().iterator();
			while (recipients.hasNext()) {
				Player rec = recipients.next();
				if (!eventChat.getRecipients().contains(rec))
					event.getRecipients().remove(rec);
			}
		}

		event.setCancelled(eventChat.isCancelled());
		event.setFormat(eventChat.getFormat() + eventChat.getMessage());
	}

	@EventHandler
	public void onTime(ServerTimeEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (chatCooldown.containsKey(player.getUniqueId())) {
				chatCooldown.put(player.getUniqueId(), chatCooldown.get(player.getUniqueId()) - 1);
				if (chatCooldown.get(player.getUniqueId()) == 0) {
					chatCooldown.remove(player.getUniqueId());
				}
			}
			if (getCoreManager().getTagManager().getPlayerTag(player) == null
					|| getCoreManager().getTagManager().getDisplayName(player) == null
					|| getCoreManager().getTagManager().getPrefixRank(player) == null) {
				player.kickPlayer("§cOcorreu um problema ao carregar seu grupo.");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().split(" ")[0].contains(":")) {
			event.setCancelled(true);
		}

		for (String s : commandBlocks) {
			if (event.getMessage().toLowerCase().startsWith(s.toLowerCase())) {
				event.setCancelled(true);
			}
		}
	}
}

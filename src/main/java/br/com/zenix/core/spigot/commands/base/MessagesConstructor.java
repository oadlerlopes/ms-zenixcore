package br.com.zenix.core.spigot.commands.base;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public enum MessagesConstructor {

	COLOR(1, "§f"),
	PREFIX(2, "§6§lZENIXCC"),
	WEBSITE(3, "www.zenix.cc"),
	WEBSTORE(4, "loja.zenix.cc"),
	MAIN_COLOR(5, "§6");

	private int id;
	private String parameters;

	private MessagesConstructor(int id, String parameters) {
		this.id = id;
		this.parameters = parameters;
	}

	public int getId() {
		return id;
	}

	public String getParameters() {
		return parameters;
	}

	public MessagesConstructor getPrefix() {
		return PREFIX;
	}

	public static void sendActionBarMessage(Player player, String message) {
		if (player != null) {
			if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() > 46) {
				IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
				PacketPlayOutChat packet = new PacketPlayOutChat(cbc, 2);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}
		} else {
			for (Player players : Bukkit.getOnlinePlayers())
				sendActionBarMessage(players, message);
		}
	}

	public static void sendClickMessage(Player player, String message, String messageClick, String message2, String cmd) {
		if (player != null) {
			IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + message + "\",\"extra\":[{\"text\":\"" + messageClick + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + message2
					+ "\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + cmd + "\"}}]}");
			PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		} else {
			for (Player players : Bukkit.getOnlinePlayers())
				sendClickMessage(players, message, messageClick, message2, cmd);
		}
	}

	public static void sendTitleMessage(Player player, String message, String message2) {
		if (player != null) {
			if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 46) {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.TITLE, ChatSerializer.a("{\"text\": \"\"}").a(message)));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(new ProtocolInjector.PacketTitle(ProtocolInjector.PacketTitle.Action.SUBTITLE, ChatSerializer.a("{\"text\": \"\"}").a(message2)));
			}
		} else {
			for (Player players : Bukkit.getOnlinePlayers())
				sendTitleMessage(players, message, message2);
		}
	}

}

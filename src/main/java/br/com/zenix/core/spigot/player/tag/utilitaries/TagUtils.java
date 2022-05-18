package br.com.zenix.core.spigot.player.tag.utilitaries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class TagUtils extends Management {

	public static final HashSet<TeamInfo> teams = new HashSet<TeamInfo>();
	private int count;

	public TagUtils(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		return true;
	}

	public void setPrefix(String playerName, String teamName, String prefix) {
		setNameTag(playerName, teamName, prefix, "");
	}

	public void setSuffix(String playerName, String teamName, String suffix) {
		setNameTag(playerName, teamName, "", suffix);
	}

	public void setPrefix(String playerName, String prefix) {
		setNameTag(playerName, "A", prefix, "");
	}

	public void setSuffix(String playerName, String suffix) {
		setNameTag(playerName, "A", "", suffix);
	}

	public void setNameTag(final String playerName, final String teamName, final String prefix, final String suffix) {
		new BukkitRunnable() {
			public void run() {
				removeTag(playerName);

				TeamInfo teamInfo = getTeam(teamName, prefix, suffix);
				if (teamInfo.getPlayers().contains(playerName))
					return;
				teamInfo.getPlayers().add(playerName);

				PacketInfo packetInfo = new PacketInfo(teamInfo.getName(), Collections.singleton(playerName), 3);
				for (Player player : Bukkit.getOnlinePlayers()) {
					packetInfo.sendToPlayer(player);
				}
			}
		}.runTask(Core.getPlugin(Core.class));
	}

	public void removeTag(String playerName) {
		TeamInfo oldTeam = getPlayerTeam(playerName);
		if (oldTeam != null) {
			oldTeam.getPlayers().remove(playerName);
			PacketInfo packetInfo = new PacketInfo(oldTeam.getName(), Collections.singleton(playerName), 4);
			for (Player player : Bukkit.getOnlinePlayers()) {
				packetInfo.sendToPlayer(player);
			}

			checkTeam(oldTeam);
		}
	}

	private TeamInfo getTeam(String teamName, String prefix, String suffix) {
		for (TeamInfo team : teams) {
			if (team.getPrefix().equals(prefix) && team.getSuffix().equals(suffix)) {
				return team;
			}
		}

		TeamInfo teamInfo = new TeamInfo(teamName + count++);
		teamInfo.setPrefix(prefix);
		teamInfo.setSuffix(suffix);
		teams.add(teamInfo);

		PacketInfo packetInfo = new PacketInfo(teamInfo.getName(), prefix, suffix, teamInfo.getPlayers(), 0);
		for (Player player : Bukkit.getOnlinePlayers()) {
			packetInfo.sendToPlayer(player);
		}

		return teamInfo;
	}

	private void checkTeam(TeamInfo teamInfo) {
		if (teamInfo.getPlayers().isEmpty()) {
			PacketInfo packetInfo = new PacketInfo(teamInfo.getName(), null, null, null, 1);
			for (Player player : Bukkit.getOnlinePlayers()) {
				packetInfo.sendToPlayer(player);
			}
			teams.remove(teamInfo);
		}
	}

	private TeamInfo getPlayerTeam(String player) {
		for (TeamInfo team : teams) {
			if (team.getPlayers().contains(player)) {
				return team;
			}
		}
		return null;
	}

	public void updateTeamsToPlayer(Player player) {
		for (TeamInfo teamInfo : teams) {
			PacketInfo packetInfo = new PacketInfo(teamInfo.getName(), teamInfo.getPrefix(), teamInfo.getSuffix(),
					teamInfo.getPlayers(), 0);
			packetInfo.sendToPlayer(player);
		}
	}

	public class PacketInfo {

		private final PacketPlayOutScoreboardTeam packet;

		public PacketInfo(String name, String prefix, String suffix, Collection<String> players, int updateType) {
			packet = new PacketPlayOutScoreboardTeam();
			
			Utils.setValue("a", packet, name);
			
			Utils.setValue("f", packet, updateType);

			if (updateType == 0 || updateType == 2) {
				
				Utils.setValue("b", packet, name);
				
				Utils.setValue("c", packet, prefix);
				
				Utils.setValue("d", packet, suffix);
				
				Utils.setValue("g", packet, 1);
			}

			if (updateType == 0) {
				addAll(players);
			}
		}

		public PacketInfo(String name, Collection<String> players, int updateType) {
			packet = new PacketPlayOutScoreboardTeam();

			if (updateType != 3 && updateType != 4) {
				throw new IllegalArgumentException("Method must be join or leave for player constructor");
			}

			if (players == null || players.isEmpty()) {
				players = new ArrayList<String>();
			}

			
			Utils.setValue("a", packet, name);
			
			Utils.setValue("f", packet, updateType);
			addAll(players);
		}

		public void sendToPlayer(Player bukkitPlayer) {
			((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packet);
		}

		@SuppressWarnings("all")
		private void addAll(Collection<String> col) {
			
			((Collection<String>) Utils.getValue("e", packet)).addAll(col);
		}
	}

	static class TeamInfo {
		private final String name;
		private String prefix;
		private String suffix;
		private Set<String> players;

		TeamInfo(String name) {
			this.name = name;
			this.players = new HashSet<String>();
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		public String getName() {
			return name;
		}

		public Set<String> getPlayers() {
			return players;
		}
	}
}

package br.com.zenix.core.spigot.player.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class ScoreboardConstructor {

	protected final Player player;
	protected final Scoreboard handle;
	protected Objective sidebarObjective;
	protected int index;

	public ScoreboardConstructor(Player tHolder) {
		this.player = tHolder;
		this.index = 15;
		this.handle = Bukkit.getScoreboardManager().getNewScoreboard();
	}

	public final void initialize(String name) {
		player.getPlayer().setScoreboard(handle);

		this.sidebarObjective = handle.registerNewObjective("sidebar", "dummy");
		this.sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.sidebarObjective.setDisplayName(name);
	}

	public final void setDisplayName(String name) {
		this.sidebarObjective.setDisplayName(name);
	}

	public final void setScore(String name, String prefix, String suffix) {
		setScore(name, prefix, suffix, index--);
	}

	public final void setScore(String name, String prefix, String suffix, int value) {
		if (name == null || name.length() > 14) {
			return;
		}
		name = name + ChatColor.RESET;
		Team scoreTeam = handle.getTeam(name);
		if (scoreTeam != null) {
			return;
		}
		scoreTeam = handle.registerNewTeam(name);
		scoreTeam.addEntry(name);

		if (prefix != null && prefix.length() <= 32) {
			scoreTeam.setPrefix(prefix);
		}
		if (suffix != null && suffix.length() <= 32) {
			scoreTeam.setSuffix(suffix);
		}

		this.sidebarObjective.getScore(name).setScore(value);
	}

	public final void updateScore(String name, String prefix, String suffix) {
		if (name == null || name.length() > 14) {
			return;
		}
		name = name + ChatColor.RESET;
		Team scoreTeam = handle.getTeam(name);
		if (scoreTeam == null) {
			return;
		}

		scoreTeam.setPrefix(prefix != null && prefix.length() <= 32 ? prefix : "");
		scoreTeam.setSuffix(suffix != null && suffix.length() <= 32 ? suffix : "");
	}

	public final void unsetScore(String name) {
		if (name == null || name.length() > 15) {
			return;
		}
		name = name + ChatColor.RESET;
		Team scoreTeam = handle.getTeam(name);
		if (scoreTeam == null) {
			return;
		}

		scoreTeam.unregister();
		this.handle.resetScores(name);
	}
	
	public final Scoreboard getHandle() {
		return handle;
	}
	
	public final Objective getSidebarObjective() {
		return sidebarObjective;
	}
}
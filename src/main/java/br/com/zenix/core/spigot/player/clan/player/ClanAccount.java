package br.com.zenix.core.spigot.player.clan.player;

import java.util.UUID;

import org.bukkit.entity.Player;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.player.clan.Clan;
import br.com.zenix.core.spigot.player.clan.ClanManager;
import br.com.zenix.core.spigot.player.clan.groups.ClanHierarchy;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */

public class ClanAccount {

	private Player player;
	private UUID uniqueId;

	private Clan clan;
	private ClanHierarchy clanHierarchy;

	public ClanAccount(Player player) {
		this.player = player;
		this.uniqueId = player.getUniqueId();

		ClanManager clanManager = Core.getCoreManager().getClanManager();

		this.clan = new Clan(clanManager.getClan(uniqueId), clanManager.getClanTag(clanManager.getClan(uniqueId)), 1);
		this.clanHierarchy = clanManager.getClanHiearchy(clanManager.getClanGroup(player.getUniqueId()));

		if (clanManager.getClanGroup(player.getUniqueId()).equalsIgnoreCase("owner"))
			this.clanHierarchy = ClanHierarchy.OWNER;
		else if (clanManager.getClanGroup(player.getUniqueId()).equalsIgnoreCase("admin"))
			this.clanHierarchy = ClanHierarchy.ADMIN;
		else if (clanManager.getClanGroup(player.getUniqueId()).equalsIgnoreCase("member"))
			this.clanHierarchy = ClanHierarchy.MEMBER;
	}
	
	public boolean isStaff() {
		if (this.clanHierarchy == ClanHierarchy.ADMIN)
			return true;
		if (this.clanHierarchy == ClanHierarchy.OWNER)
			return true;
		return false;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public ClanHierarchy getClanHierarchy() {
		return clanHierarchy;
	}

	public Clan getClan() {
		return clan;
	}

	public Player getPlayer() {
		return player;
	}

}

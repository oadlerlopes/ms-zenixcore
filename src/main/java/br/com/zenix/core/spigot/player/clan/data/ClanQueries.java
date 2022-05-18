package br.com.zenix.core.spigot.player.clan.data;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public enum ClanQueries {

	CREATE_CLAN("INSERT INTO znx_clan_data (name, tag, kills, deaths, wins, xp) VALUES (?, ?, ?, ?, ?, ?)"),	
	ADD_PLAYER_CLAN("INSERT INTO znx_clan_player_data (name, uuid, stat) VALUES (?, ?, ?)"),	
	CHECK_PLAYER_CLAN("SELECT * FROM znx_clan_player_data WHERE UUID=?"),
	CHECK_CLAN("SELECT * FROM znx_clan_data WHERE name= ?"),
	CHECK_TAG("SELECT * FROM znx_clan_data WHERE tag= ?"),
	GET_CLAN_PLAYER("SELECT * FROM znx_clan_player_data WHERE uuid= ?"),
	GET_CLAN_PLAYER_NAME("SELECT * FROM znx_clan_player_data WHERE name= ?"),
	GET_CLAN_VAR("SELECT * FROM znx_clan_data WHERE name= ?"),
	REMOVE_CLAN("DELETE FROM znx_clan_data WHERE name='?'"),
	REMOVE_CLAN_PLAYERS("DELETE FROM znx_clan_player_data WHERE name='?'"),
	REMOVE_PLAYER_CLAN("DELETE FROM znx_clan_player_data WHERE uuid='?'");

	private final String query;

	private ClanQueries(String query) {
		this.query = query;
	}

	public String toString() {
		return query;
	}
}

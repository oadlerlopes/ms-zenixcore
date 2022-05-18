package br.com.zenix.core.proxy.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.zenix.core.spigot.server.type.ServerType;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class FullyServerStatus {

	private static final Gson gson = new Gson();

	private final int port;
	private final ServerType serverType;

	private int players, maxPlayers;
	private String name, customMessage;

	private ServerStatus serverStatus;

	public FullyServerStatus(int port, String name, ServerType serverType, ServerStatus serverStatus, int players,
			int maxPlayers, String customMessage) {
		this.port = port;
		this.name = name;
		this.serverType = serverType;
		this.serverStatus = serverStatus;
		this.players = players;
		this.maxPlayers = maxPlayers;
		this.customMessage = customMessage;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	public int getPlayers() {
		return players;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setServerStatus(ServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();

		jsonObject.add("port", gson.toJsonTree(port));
		jsonObject.add("name", gson.toJsonTree(name));
		jsonObject.add("type", gson.toJsonTree(serverType.getId()));
		jsonObject.add("status", gson.toJsonTree(serverStatus.getStatus()));
		jsonObject.add("players", gson.toJsonTree(players));
		jsonObject.add("maxplayers", gson.toJsonTree(maxPlayers));
		jsonObject.add("message", gson.toJsonTree(customMessage));

		return jsonObject;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public static FullyServerStatus decode(String json) {
		if (json == null) {
			return null;
		} else if (json.equals("")) {
			return null;
		}
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		return new FullyServerStatus(jsonObject.get("port").getAsInt(), jsonObject.get("name").getAsString(),
				ServerType.getServerType(jsonObject.get("type").getAsInt()),
				ServerStatus.getServerStatus(jsonObject.get("status").getAsInt()), jsonObject.get("players").getAsInt(),
				jsonObject.get("maxplayers").getAsInt(), jsonObject.get("message").getAsString());
	}

}

package br.com.zenix.core.proxy.manager.managements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.com.zenix.core.proxy.player.account.ProxyAccount;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PermissionManager {

	public static final HashMap<UUID, List<String>> playerPermissions = new HashMap<>();
	
	public static boolean updatePermissions(ProxyAccount account) {
		proccessPermissionsList(account.getPlayer(), account.getGroup().getPermissions());
		return true;
	}

	public static void proccessPermissionsList(ProxiedPlayer player, Map<String, Boolean> map) {
		if (map.isEmpty())
			return;

		for (String permission : map.keySet()) {
			boolean active = map.get(permission);
			if (active)
				addPermission(player, permission);
			else
				removePermission(player, permission);
		}
	}

	public static void addPermission(UUID id, String permission) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(id);
		if (p == null) {
			return;
		}
		addPermission(p, permission);
	}

	public static void addPermission(ProxiedPlayer p, String permission) {
		if (permission.startsWith("-")) {
			p.setPermission(permission.substring(1), false);
		} else {
			p.setPermission(permission, true);
		}
		List<String> permissions = (playerPermissions.containsKey(p.getUniqueId()) ? playerPermissions.get(p.getUniqueId()) : new ArrayList<>());
		permissions.add(permission);
		playerPermissions.put(p.getUniqueId(), permissions);
	}

	public static void addPermissions(ProxiedPlayer p, String... permissions) {
		for (String permission : permissions) {
			addPermission(p, permission);
		}
	}

	public static void removePermissions(ProxiedPlayer p, String... permissions) {
		for (String permission : permissions) {
			removePermission(p, permission);
		}
	}

	public static void removePermission(String id, String permission) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(UUID.fromString(id));
		if (p == null) {
			return;
		}
		removePermission(p, permission);
	}

	public static void removePermission(ProxiedPlayer p, String permission) {
		if (!permission.startsWith("-")) {
			p.setPermission(permission, false);
		}
		List<String> permissions = (playerPermissions.containsKey(p.getUniqueId()) ? playerPermissions.get(p.getUniqueId()) : new ArrayList<>());
		permissions.remove(permission);
		playerPermissions.put(p.getUniqueId(), permissions);
	}

	public static HashMap<UUID, List<String>> getPlayerPermissions() {
		return playerPermissions;
	}

}

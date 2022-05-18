package br.com.zenix.core.proxy.manager.managements;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.zenix.core.plugin.data.jedis.JedisExecutor;
import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import br.com.zenix.core.proxy.server.FullyServerStatus;
import br.com.zenix.core.proxy.server.ServerStatus;
import br.com.zenix.core.proxy.utilitaries.StringUtils;
import br.com.zenix.core.spigot.server.type.ServerType;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.config.ServerInfo;
import redis.clients.jedis.JedisPubSub;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class PingManager extends SimpleHandler {

	private static final HashMap<String, List<CustomMotd>> motds = new HashMap<>();
	private static final HashMap<Integer, FullyServerStatus> status = new HashMap<>();

	private static final HashMap<String, ServerPing> serversPing = new HashMap<>();

	private ServerPing lastServerPing = null;

	private ServerPing offline, inexistent;

	public PingManager(ProxyManager manager) {
		super(manager);
	}

	public boolean initialize() {
		offline = new ServerPing(new Protocol("§70§8/§70", 1000), new Players(0, 0, getPlayerInfo()),
				StringUtils.makeCenteredMotd("§8§m--§b§m]--§6§l Zenix§f | §dMinecraft Server§b §m--[§8§m--§f")
						+ "\n§r" + StringUtils.makeCenteredMotd(""),
				(Favicon) null);
		inexistent = new ServerPing(new Protocol("§70§8/§70", 1000), new Players(0, 0, getPlayerInfo()),
				StringUtils.makeCenteredMotd("§8§m--§b§m]--§6§l Zenix§f | §dMinecraft Server§b §m--[§8§m--§f")
						+ "\n§r" + StringUtils.makeCenteredMotd(""),
				(Favicon) null);

		return startLoad();
	}

	public boolean startLoad() {
		getProxyManager().getPlugin().getProxy().getScheduler().schedule(getProxyManager().getPlugin(), () -> {
			initializeStatus();
		}, 1, TimeUnit.MILLISECONDS);

		getProxyManager().getPlugin().getProxy().getScheduler().schedule(getProxyManager().getPlugin(), () -> {
			getProxyManager().getPlugin().getProxy().getScheduler().runAsync(getProxyManager().getPlugin(), () -> {
				updatePings();

				FullyServerStatus status = new FullyServerStatus(-1, "ALL", ServerType.ALL, ServerStatus.ONLINE,
						getLastServerPing().getPlayers().getOnline(), getLastServerPing().getPlayers().getMax(), "");

				getProxyManager().getJedis().getSecondaryJedis().publish(JedisExecutor.SERVER_INFO_DATA_CHANNEL,
						status.toJson().toString());
			});
		}, 2, 2, TimeUnit.SECONDS);
		return true;
	}

	public void initializeStatus() {
		getProxyManager().getJedis().getJedis().subscribe(new JedisPubSub() {
			public void onMessage(String channel, String message) {
				FullyServerStatus status = FullyServerStatus.decode(message);
				getAllStatus().put(status.getPort(), status);
			}
		}, JedisExecutor.SERVER_INFO_DATA_CHANNEL);
	}

	public boolean getCanJoin(Integer server) {
		if (status.containsKey(server)) {
			ServerStatus status = getAllStatus().get(server).getServerStatus();
			if (status == ServerStatus.ONLINE || status == ServerStatus.PREGAME || status == ServerStatus.STARTING) {
				return true;
			}
		}
		return false;
	}

	public FullyServerStatus getServerStatus(Integer port) {
		return (status.containsKey(port) ? status.get(port) : null);
	}

	public ServerPing getServerPing(String name) {
		return (serversPing.containsKey(name) ? serversPing.get(name) : null);
	}

	public boolean updatePings() {
		try {
			for (ServerInfo server : getProxyManager().getPlugin().getProxy().getServers().values()) {
				server.ping(new Callback<ServerPing>() {
					public void done(ServerPing ping, Throwable thraw) {
						if (thraw == null) {
							serversPing.put(server.getName().toLowerCase(), ping);
						} else {
							serversPing.put(server.getName().toLowerCase(), getOffline());
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setLastServerPing(ServerPing lastServerPing) {
		this.lastServerPing = lastServerPing;
	}

	public ServerPing getLastServerPing() {
		return lastServerPing;
	}

	public ServerPing getOffline() {
		return offline;
	}

	public ServerPing getInexistent() {
		return inexistent;
	}

	public HashMap<String, List<CustomMotd>> getMotds() {
		return motds;
	}

	public HashMap<Integer, FullyServerStatus> getAllStatus() {
		return status;
	}

	public HashMap<String, ServerPing> getServersPing() {
		return serversPing;
	}

	public PlayerInfo[] getPlayerInfo() {
		return new PlayerInfo[] {  };
	}

	public static final class CustomMotd {

		private final int id;
		private String motd;

		public CustomMotd(int id, String motd) {
			this.id = id;
			this.motd = motd;
		}

		public int getId() {
			return id;
		}

		public String getMotd() {
			return motd;
		}
	}

}

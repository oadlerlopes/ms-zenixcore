package br.com.zenix.core.proxy.manager;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import br.com.zenix.core.networking.PacketProxyHandler;
import br.com.zenix.core.plugin.data.jedis.JedisHandler;
import br.com.zenix.core.plugin.data.mysql.MySQL;
import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.proxy.Proxy;
import br.com.zenix.core.proxy.file.FileManager;
import br.com.zenix.core.proxy.manager.managements.GroupManager;
import br.com.zenix.core.proxy.manager.managements.MotdManager;
import br.com.zenix.core.proxy.manager.managements.PingManager;
import br.com.zenix.core.proxy.manager.managements.RedirectManager;
import br.com.zenix.core.proxy.player.account.ProxyAccountManager;
import br.com.zenix.core.proxy.twitter.TwitterManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */

public class ProxyManager {

	private static final boolean WINDOWS_SYSTEM = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;

	private static final List<String> messages = new ArrayList<>();

	private JedisHandler jedis;

	private PacketProxyHandler packetProxyHandler;
	private FileManager fileManager;
	private ProxyAccountManager accountManager;
	private GroupManager groupManager;
	private PingManager pingManager;
	private MotdManager motdManager;
	private TwitterManager twitterManager;

	private Utils utils;
	private Proxy coreProxy;
	private RedirectManager redirectManager;
	private MySQL mysql;

	public int size = 2;
	public int players = 0;

	private boolean maintenance = false;

	public ProxyManager(Proxy coreProxy) {
		this.coreProxy = coreProxy;
		utils = new Utils();

		if (WINDOWS_SYSTEM) {

			mysql = new MySQL(coreProxy.getLoggerSecondary(), "root", "", "jdbc:mysql://localhost:3306", "wmc");

			if (mysql.openConnection()) {
				getLogger().log("The mysql connection have been established.");
			} else {
				getLogger().error("Error the mysql connection have not been established.");
				return;
			}

			jedis = new JedisHandler(getLogger(), "localhost", "", 6379);

			if (jedis.openConnections()) {
				getLogger().log("The redis connection have been established.");
			} else {
				getLogger().error("Error the redis connection have not been established.");
				return;
			}

		} else {
			mysql = new MySQL(coreProxy.getLoggerSecondary(), "root", "adlerlopes5340139@1adler",
					"jdbc:mysql://localhost:3306", "zenix");

			if (mysql.openConnection()) {
				getLogger().log("The mysql connection have been established.");
			} else {
				getLogger().error("Error the mysql connection have not been established.");
				return;
			}

			jedis = new JedisHandler(getLogger(), "localhost", "P19414788928073704541224", 6379);

			if (jedis.openConnections()) {
				getLogger().log("The redis connection have been established.");
			} else {
				getLogger().error("Error the redis connection have not been established.");
				return;
			}
		}

		packetProxyHandler = new PacketProxyHandler(this);
		if (!packetProxyHandler.correctlyStart())
			return;

		fileManager = new FileManager(this);
		if (!fileManager.correctlyStart())
			return;

		pingManager = new PingManager(this);
		if (!pingManager.correctlyStart())
			return;

		twitterManager = new TwitterManager(this);
		if (!twitterManager.correctlyStart())
			return;
		
		accountManager = new ProxyAccountManager(this);
		if (!accountManager.correctlyStart())
			return;

		redirectManager = new RedirectManager(this);
		if (!redirectManager.correctlyStart())
			return;

		groupManager = new GroupManager(this);
		if (!groupManager.correctlyStart())
			return;

		motdManager = new MotdManager(this);
		if (!motdManager.correctlyStart())
			return;

		br.com.zenix.core.proxy.loader.ClassLoader classLoader = new br.com.zenix.core.proxy.loader.ClassLoader(this);

		if (!classLoader.correctlyStart())
			return;
	}
	
	public String getServerIP(String serverName) {
		if (serverName.toLowerCase().startsWith("hg-")) {
			return "a" + serverName.replace("HG-", "") + ".hg.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("evento-")) {
			return "a" + serverName.replace("EVENTO-", "") + ".event.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("oitc-")) {
			return "a" + serverName.replace("OITC-", "") + ".oitc.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("gladiator-")) {
			return "a" + serverName.replace("GLADIATOR-", "") + ".gladiator.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("pvp-")) {
			if (serverName.equals("PVP-1")) {
				return "a" + serverName.replace("PVP-", "") + ".fulliron.pvp.zenix.cc";
			} else {
				return "a" + serverName.replace("PVP-", "") + ".simulator.pvp.zenix.cc";
			}
		}
		if (serverName.toLowerCase().startsWith("lobby-")) {
			return "a" + serverName.replace("LOBBY-", "") + ".lobby.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("ss-")) {
			return "a" + serverName.replace("SS-", "") + ".screenshare.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("practice-")) {
			return "practice.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("sw-")) {
			return "game.zenix.cc";
		}
		return "Server does not match the defaults to inform the IP.";
	}
	
	public TwitterManager getTwitterManager() {
		return twitterManager;
	}

	public MotdManager getMotdManager() {
		return motdManager;
	}

	public PacketProxyHandler getPacketHandler() {
		return packetProxyHandler;
	}

	public JedisHandler getJedis() {
		return jedis;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public MySQL getMySQL() {
		return mysql;
	}

	public ProxyAccountManager getAccountManager() {
		return accountManager;
	}

	public RedirectManager getRedirectManager() {
		return redirectManager;
	}

	public PingManager getPingManager() {
		return pingManager;
	}

	public Proxy getPlugin() {
		return coreProxy;
	}

	public Utils getUtils() {
		return utils;
	}

	public List<String> getMessages() {
		return messages;
	}

	public boolean isMaintenance() {
		return maintenance;
	}

	public void setMaintenance(boolean maintence) {
		this.maintenance = maintence;
		try {
			PreparedStatement preparedStatement = getMySQL().getConnection()
					.prepareStatement(CoreQueries.SERVER_CONFIG_UPDATE.toString());
			preparedStatement.setInt(1, maintence ? 1 : 0);
			preparedStatement.setString(2, "maintenance");
			preparedStatement.execute();
			preparedStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Logger getLogger() {
		return getPlugin().getLoggerSecondary();
	}

}

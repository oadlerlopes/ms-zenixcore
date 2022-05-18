package br.com.zenix.core.spigot.manager;

import java.io.File;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.networking.PacketHandler;
import br.com.zenix.core.plugin.data.lang.LangManager;
import br.com.zenix.core.plugin.data.management.DataManager;
import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.plugin.utilitaries.Utils;
import br.com.zenix.core.proxy.server.FullyServerStatus;
import br.com.zenix.core.proxy.server.ServerStatus;
import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.anticheat.ZencheatManager;
import br.com.zenix.core.spigot.anticheat.lag.LagCore;
import br.com.zenix.core.spigot.bo2.BO2Constructor;
import br.com.zenix.core.spigot.manager.management.Unloader;
import br.com.zenix.core.spigot.manager.managements.ConfigurationManager;
import br.com.zenix.core.spigot.player.account.AccountManager;
import br.com.zenix.core.spigot.player.clan.ClanManager;
import br.com.zenix.core.spigot.player.clan.player.ClanAccountManager;
import br.com.zenix.core.spigot.player.events.ServerTimeEvent;
import br.com.zenix.core.spigot.player.fetcher.FetcherManager;
import br.com.zenix.core.spigot.player.permissions.PermissionManager;
import br.com.zenix.core.spigot.player.punish.PunishManager;
import br.com.zenix.core.spigot.player.skin.SkinManager;
import br.com.zenix.core.spigot.player.tag.TagManager;
import br.com.zenix.core.spigot.player.tag.utilitaries.TagUtils;
import br.com.zenix.core.spigot.server.type.ServerType;
import br.com.zenix.core.spigot.twitter.TwitterManager;
import br.com.zenix.core.spigot.world.WorldEditManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public class CoreManager {

	private final Core core;
	private final Utils utils;

	private final FullyServerStatus serverStatus;

	private final String serverName;

	private DataManager dataManager;
	private ConfigurationManager configurationManager;
	private TagManager tagManager;
	private TagUtils tagUtils;

	private AccountManager accountManager;
	private PermissionManager permissionManager;
	private FetcherManager fetcherManager;

	private ZencheatManager zencheatManager;

	private PunishManager punishManager;
	private BO2Constructor bo2;

	private Unloader unloader;
	private PacketHandler packetHandler;

	private Random random;
	
	private ClanManager clanManager;
	private ClanAccountManager clanAccountManager;

	private SkinManager skinManager;
	private TwitterManager twitterManager;
	private WorldEditManager worldEditManager;

	private LagCore lagCore;

	private LangManager langManager;

	private br.com.zenix.core.spigot.manager.managements.ClassLoader classLoader;

	private ServerType serverType = ServerType.NONE;

	public CoreManager(Core core) {
		this.core = core;

		getPlugin().saveDefaultConfig();

		utils = new Utils();

		serverName = new File(getPlugin().getDataFolder().getParentFile().getAbsolutePath()).getParentFile().getName()
				.toUpperCase();

		serverStatus = new FullyServerStatus(getPlugin().getServer().getPort(), serverName, getServerType(),
				ServerStatus.STARTING, 0, getPlugin().getServer().getMaxPlayers(), "Servidor iniciando.");

		configurationManager = new ConfigurationManager(this);
		if (!configurationManager.correctlyStart()) {
			return;
		}

		serverType = ServerType.getServerType(getPlugin().getConfig().getString("type"));
		getLogger().log("Changing the server to " + serverType.getName() + ".");

		random = new Random();

		dataManager = new DataManager(this);
		if (!dataManager.correctlyStart()) {
			return;
		}

		tagManager = new TagManager(this);
		if (!tagManager.correctlyStart()) {
			return;
		}

		tagUtils = new TagUtils(this);
		if (!tagUtils.correctlyStart()) {
			return;
		}

		permissionManager = new PermissionManager(this);
		if (!permissionManager.correctlyStart()) {
			return;
		}

		lagCore = new LagCore(this);
		if (!lagCore.correctlyStart()) {
			return;
		}

		zencheatManager = new ZencheatManager(this);
		if (!zencheatManager.correctlyStart()) {
			return;
		}

		accountManager = new AccountManager(this);
		if (!accountManager.correctlyStart()) {
			return;
		}

		fetcherManager = new FetcherManager(this);
		if (!fetcherManager.correctlyStart()) {
			return;
		}

		punishManager = new PunishManager(this);
		if (!punishManager.correctlyStart()) {
			return;
		}

		packetHandler = new PacketHandler(this);
		if (!packetHandler.correctlyStart()) {
			return;
		}

		skinManager = new SkinManager(this);
		if (!skinManager.correctlyStart()) {
			return;
		}

		twitterManager = new TwitterManager(this);
		if (!twitterManager.correctlyStart()) {
			return;
		}

		bo2 = new BO2Constructor(this);
		if (!bo2.correctlyStart()) {
			return;
		}

		worldEditManager = new WorldEditManager(this);
		if (!worldEditManager.correctlyStart()) {
			return;
		}

		unloader = new Unloader(this);
		if (!unloader.correctlyStart()) {
			return;
		}

		classLoader = new br.com.zenix.core.spigot.manager.managements.ClassLoader(this);
		if (!classLoader.correctlyStart()) {
			return;
		}

		langManager = new LangManager(this);
		if (!langManager.correctlyStart()) {
			return;
		}

		clanManager = new ClanManager(this);
		if (!clanManager.correctlyStart()) {
			return;
		}
		clanAccountManager = new ClanAccountManager(this);
		if (!clanAccountManager.correctlyStart()) {
			return;
		}

		new BukkitRunnable() {
			public void run() {
				Bukkit.getPluginManager().callEvent(new ServerTimeEvent());
			}
		}.runTaskTimer(getPlugin(), 20L, 20L);

		getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(getPlugin(), "BungeeCord");
		getPlugin().getServer().getMessenger().registerIncomingPluginChannel(getPlugin(), "BungeeCord",
				new PluginMessageListener() {
					public void onPluginMessageReceived(String channel, Player dontMatter, byte[] bytes) {

					}
				});

		getLogger().log("The plugin " + getPlugin().getName() + " version " + getPlugin().getDescription().getVersion()
				+ " was started correcly.");

		core.setCorrectlyStarted(true);
	}

	public ClanManager getClanManager() {
		return clanManager;
	}

	public ZencheatManager getRavenManager() {
		return zencheatManager;
	}

	public LagCore getLag() {
		return lagCore;
	}

	public WorldEditManager getWorldEditManager() {
		return worldEditManager;
	}
	
	public ClanAccountManager getClanAccountManager() {
		return clanAccountManager;
	}

	public Random getRandom() {
		return random;
	}

	public PunishManager getPunishManager() {
		return punishManager;
	}

	public BO2Constructor getBO2() {
		return bo2;
	}

	public SkinManager getSkinManager() {
		return skinManager;
	}

	public PacketHandler getPacketHandler() {
		return packetHandler;
	}

	public boolean setServerStatus(ServerStatus status) {
		getServerStatus().setPlayers(Bukkit.getOnlinePlayers().size());
		getServerStatus().setServerStatus(status);
		return true;
	}

	public boolean setServerStatus(String message) {
		getServerStatus().setCustomMessage(message);
		return setServerStatus(getServerStatus().getServerStatus());
	}

	public boolean setServerStatus(ServerStatus status, String message) {
		getServerStatus().setCustomMessage(message);
		return setServerStatus(status);
	}

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public TwitterManager getTwitterManager() {
		return twitterManager;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public FetcherManager getNameFetcher() {
		return fetcherManager;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public TagUtils getTagUtils() {
		return tagUtils;
	}

	public FullyServerStatus getServerStatus() {
		return serverStatus;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public LangManager getLangManager() {
		return langManager;
	}

	public FileConfiguration getConfig() {
		return getConfigurationManager().getConfig();
	}

	public void registerListener(Listener listener) {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(listener, getPlugin());
	}

	public Logger getLogger() {
		return core.getLoggerSecondary();
	}

	public Core getPlugin() {
		return core;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerIP() {
		if (serverName.toLowerCase().startsWith("hg-")) {
			return "a" + serverName.replace("HG-", "") + ".hg.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("evento-")) {
			return "a" + serverName.replace("EVENTO-", "") + ".event.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("oitc-")) {
			return "a" + serverName.replace("OITC-", "") + ".oitc.zenix.cc";
		}
		if (serverName.toLowerCase().startsWith("thebridge-")) {
			return "a" + serverName.replace("thebridge-", "") + ".bridge.zenix.cc";
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

	public br.com.zenix.core.spigot.manager.managements.ClassLoader getClassLoader() {
		return classLoader;
	}

	public Core getCore() {
		return core;
	}

	public Utils getUtils() {
		return utils;
	}
}

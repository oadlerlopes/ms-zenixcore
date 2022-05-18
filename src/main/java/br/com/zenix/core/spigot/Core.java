package br.com.zenix.core.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import br.com.zenix.core.plugin.data.jedis.JedisExecutor;
import br.com.zenix.core.plugin.logger.Logger;
import br.com.zenix.core.proxy.server.FullyServerStatus;
import br.com.zenix.core.proxy.server.ServerStatus;
import br.com.zenix.core.spigot.manager.CoreManager;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of this file,
 * via any medium is strictly prohibited proprietary and confidential
 */
public abstract class Core extends JavaPlugin {

	protected static CoreManager coreManager;

	private static Logger logger;
	private boolean correctlyStarted = false, stopping = false;

	private ProtocolManager protocolManager;

	@Override
	public void onLoad() {
		logger = new Logger(getLogger(), null, true);
		logger.log("Starting to load the plugin.");
		protocolManager = ProtocolLibrary.getProtocolManager();

		logger.log("Protocol = " + protocolManager);
	}

	@Override
	public void onEnable() {
		coreManager = new CoreManager(this);
		coreManager.setServerStatus(ServerStatus.ONLINE);
	}

	public void onDisable() {
		if (isCorrectlyStarted()) {
			handleStop();

			coreManager.getLogger().log("Stopping all activities of the plugin.");
		}
	}

	public Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		}

		catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static CoreManager getCoreManager() {
		return coreManager;
	}

	public Logger getLoggerSecondary() {
		return logger;
	}

	public ProtocolManager getProtocolManager() {
		return protocolManager;
	}

	public boolean isCorrectlyStarted() {
		return correctlyStarted;
	}

	public void setCorrectlyStarted(boolean correctlyStarted) {
		this.correctlyStarted = correctlyStarted;
	}

	public void handleStop() {
		if (stopping)
			return;

		stopping = true;
		for (Player player : Bukkit.getOnlinePlayers())
			player.kickPlayer("§cO servidor que você estava conectado foi desligado.");

		FullyServerStatus status = getCoreManager().getServerStatus();
		status.setPlayers(0);
		status.setServerStatus(ServerStatus.OFFLINE);

		getCoreManager().setServerStatus(status.getServerStatus());
		getCoreManager().getDataManager().getJedisHandler().getJedis().publish(JedisExecutor.SERVER_INFO_DATA_CHANNEL,
				getCoreManager().getServerStatus().toString());

		Bukkit.shutdown();
	}
}

package br.com.zenix.core.plugin.data.management;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import br.com.zenix.core.plugin.data.jedis.JedisHandler;
import br.com.zenix.core.plugin.data.management.utilitaries.Callback;
import br.com.zenix.core.plugin.data.mysql.MySQL;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class DataManager extends Management {

	private JedisHandler jedisHandler;
	private MySQL mySQL;

	private String addressSql, usernameSql, passwordSql, schemaSql;
	private int portSql;

	private String addressRedis, passwordRedis;
	private int portRedis;

	public DataManager(CoreManager coreManager) {
		super(coreManager);

		this.addressSql = "localhost";
		this.portSql = 3306;
		this.usernameSql = "root";
		this.passwordSql = "";
		this.schemaSql = "wave";

		this.addressRedis = "127.0.0.1";
		this.portRedis = 6379;
		this.passwordRedis = "";

	}

	public boolean openConnection() {
		try {

			mySQL = new MySQL(getLogger(), usernameSql, passwordSql, "jdbc:mysql://" + addressSql + ":" + portSql, schemaSql);

			if (mySQL.openConnection()) {
				getLogger().log("The primary and secondary connections have been established.");
			} else {
				getLogger().error("Error the mysql connections have not been established.");
				return false;
			}

			getLogger().log("Trying to create those mysql tables if they doesnt exists.");
			getLogger().log("All those mysql tables was created if they doesnt exists.");

			jedisHandler = new JedisHandler(getLogger(), addressRedis, passwordRedis, portRedis);

			if (jedisHandler.openConnections()) {
				getLogger().log("The redis connection have been established.");
			} else {
				getLogger().error("Error the redis connection have not been established.");
				return false;
			}

		} catch (Exception e) {
			getLogger().error("Error when the mysql connections tries have not been established.");
			return false;
		}
		return true;
	}

	public boolean initialize() {
		try {

			ConfigurationSection connectionSection = getCoreManager().getConfig().getConfigurationSection("database-connection");
			if (connectionSection == null) {
				getLogger().log(Level.WARNING, "Error to find the database-connection section on the config. Using default values!");
			} else {
				if (connectionSection.contains("address")) {
					this.addressSql = connectionSection.getString("address");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the address in the config file. Using the default %s value", addressSql);
				}
				if (connectionSection.contains("port")) {
					this.portSql = connectionSection.getInt("port");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the port in the config file. Using the default %s value", portSql);
				}
				if (connectionSection.contains("username")) {
					this.usernameSql = connectionSection.getString("username");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the username in the config file. Using the default %s value", usernameSql);
				}
				if (connectionSection.contains("password")) {
					this.passwordSql = connectionSection.getString("password");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the password in the config file. Using the default %s value", passwordSql);
				}
				if (connectionSection.contains("schema")) {
					this.schemaSql = connectionSection.getString("schema");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the schema in the config file. Using the default %s value", schemaSql);
				}
			}

			ConfigurationSection redisSection = getCoreManager().getConfig().getConfigurationSection("redis-connection");
			if (redisSection == null) {
				getLogger().log(Level.WARNING, "Error to find the database-connection section on the config. Using default values!");
			} else {
				if (redisSection.contains("address")) {
					this.addressRedis = redisSection.getString("address");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the address in the config file. Using the default %s value", addressRedis);
				}
				if (redisSection.contains("port")) {
					this.portRedis = redisSection.getInt("port");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the port in the config file. Using the default %s value", portRedis);
				}
				if (redisSection.contains("password")) {
					this.passwordRedis = redisSection.getString("password");
				} else {
					getLogger().log(Level.WARNING, "The plugin dont find the password in the config file. Using the default %s value", passwordRedis);
				}

			}

			return openConnection();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, e, "Error to load the config file, in the database-connection section, details below:");
			return false;
		}
	}

	public JedisHandler getJedisHandler() {
		return jedisHandler;
	}

	public MySQL getMySQL() {
		return mySQL;
	}

	public void playerExists(UUID uuid, String table, Callback<Boolean> callback) {
		try {
			ResultSet resultSet = getMySQL().executeQuery("SELECT * FROM " + table + " WHERE uuid= " + uuid + ";");
			callback.finish(resultSet.next());
			resultSet.close();
		} catch (Exception e) {
			getLogger().error("Error to check if the player " + uuid + " exists on the table " + table + ".", e);
		}
	}

}


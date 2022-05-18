package br.com.zenix.core.plugin.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.com.zenix.core.plugin.data.mysql.queries.CoreQueries;
import br.com.zenix.core.plugin.logger.Logger;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class MySQL {

	private static final Executor asyncExecutor = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Async Thread").build());

	private final Logger logger;
	private final String user, pass, url, database;

	private Connection conn, slave;

	public MySQL(Logger logger, String user, String pass, String url, String database) {
		this.logger = logger;
		this.user = user;
		this.pass = pass;
		this.url = url;
		this.database = database;
	}

	public boolean openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			conn = DriverManager.getConnection(url, user, pass);
			slave = DriverManager.getConnection(url, user, pass);

			conn.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS `" + database + "` DEFAULT CHARACTER SET utf8 ;");

			conn.createStatement().executeQuery("USE `" + database + "`;");
			slave.createStatement().executeQuery("USE `" + database + "`;");

			return insertDefaults();
		} catch (Exception exception) {
			logger.error("Impossible to create the connection of mysql with url: " + url + "/" + database + " and user " + user + ".", exception);
		}
		return false;
	}

	public boolean createTables(String query) {
		return executeUpdate(query);
	}

	public boolean createTables(CoreQueries query) {
		return executeUpdate(query.toString());
	}

	public ResultSet executeQuery(String query) {
		try {
			return getConnection().createStatement().executeQuery(query);
		} catch (Exception exception) {
			logger.error("Impossible to execute the mysql query (" + query + ").", exception);
		}
		return null;
	}

	public boolean executeAsyncUpdate(String update) {
		AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		asyncExecutor.execute(() -> {
			try {
				PreparedStatement statement = getSlaveConnection().prepareStatement(update);
				statement.execute();
				statement.close();
				atomicBoolean.set(true);
			} catch (Exception exception) {
				logger.error("Impossible to execute a async mysql update (" + update + ").", exception);
			}
		});
		return atomicBoolean.get();
	}

	public boolean executeUpdate(String update) {
		try {
			PreparedStatement statement = getSlaveConnection().prepareStatement(update);
			statement.execute();
			statement.close();
			return true;
		} catch (Exception exception) {
			logger.error("Impossible to execute a sync mysql update (" + update + ").", exception);
		}
		return false;
	}

	public boolean insertDefaults() {
		try {
			for (CoreQueries query : CoreQueries.values()) {
				String[] name = query.toString().split("`");
				if (query.name().startsWith("TABLE")) {
					logger.debug("Trying to create the table " + name[1] + " to the mysql database.");
					createTables(query);
				}
			}

			for (CoreQueries query : CoreQueries.values()) {
				String[] name = query.toString().split("`");
				if (query.name().startsWith("DEFAULT")) {
					logger.debug("Inserting the default value to the table " + name[1] + " to the mysql database.");
					executeUpdate(query.toString());
				}
			}
		} catch (Exception exception) {
			logger.error("Impossible to insert the default values on the schema.", exception);
			return false;
		}
		return true;
	}

	public Connection getConnection() {
		return conn;
	}

	public Connection getSlaveConnection() {
		return slave;
	}

	public Executor getAsyncExecutor() {
		return asyncExecutor;
	}
}

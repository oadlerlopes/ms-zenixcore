package br.com.zenix.core.plugin.data.jedis;

import java.util.logging.Level;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import br.com.zenix.core.plugin.logger.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class JedisHandler {

	private final Logger logger;
	private final String address, password;
	private final int port, maxConnections, minConnections, idleTimeout;

	protected JedisPool connectionPool;
	protected GenericObjectPoolConfig connectionPoolConfig;
	protected Jedis jedis, secondary;

	public JedisHandler(Logger logger, String address, String pass, int port) {
		this.logger = logger;
		this.address = address;
		this.password = pass;
		this.port = port;

		this.maxConnections = 1000;
		this.minConnections = 1;
		this.idleTimeout = 30000;
	}

	public boolean openConnections() {
		this.connectionPoolConfig = new GenericObjectPoolConfig();
		this.connectionPoolConfig.setMinIdle(minConnections);
		this.connectionPoolConfig.setMaxTotal(maxConnections);
		this.connectionPoolConfig.setMaxIdle(idleTimeout);
		this.connectionPoolConfig.setFairness(true);

		return initialize();
	}

	public boolean initialize() {
		try {
			this.connectionPool = new JedisPool(connectionPoolConfig, address, port);
			jedis = this.connectionPool.getResource();
			if (password != null && password.length() > 0) {
				jedis.auth(password);
			}

			secondary = this.connectionPool.getResource();
			if (password != null && password.length() > 0) {
				secondary.auth(password);
			}

			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, e, "Ocorreu um erro ao inicializar a conexao redis! Detalhes abaixo:");
			return false;
		}
	}

	protected Jedis updateJedis() {
		try {
			Jedis jedis = this.connectionPool.getResource();
			if (password != null && password.length() > 0) {
				jedis.auth(password);
			}
			this.jedis = jedis;
			return jedis;
		} catch (Exception e) {
			return null;
		}
	}

	public Jedis getJedis() {
		return jedis;
	}

	public Jedis getSecondaryJedis() {
		return secondary;
	}

}

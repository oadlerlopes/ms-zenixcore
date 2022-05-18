package br.com.zenix.core.plugin.data.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Copyright (C) Zenix, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public abstract class JedisExecutor extends JedisPubSub implements Runnable {

	public static final String SERVER_INFO_DATA_CHANNEL = "@SERVER_INFO";

	private final Jedis jedis;
	private final String channel;

	public JedisExecutor(Jedis jedis, String channel) {
		this.jedis = jedis;
		this.channel = channel;
	}

	public void run() {
		try {
			jedis.subscribe(this, channel);
		} finally {
			this.jedis.close();
		}
	}

	public abstract void onMessage(String channel, String message);

}

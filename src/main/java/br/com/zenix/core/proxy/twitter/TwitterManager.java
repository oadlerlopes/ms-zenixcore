package br.com.zenix.core.proxy.twitter;

import br.com.zenix.core.proxy.manager.ProxyManager;
import br.com.zenix.core.proxy.manager.managements.constructor.SimpleHandler;
import br.com.zenix.core.spigot.twitter.TwitterAccount;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Copyright (C) Adler Lopes, all rights reserved unauthorized copying of this
 * file, via any medium is strictly prohibited proprietary and confidential
 */
public class TwitterManager extends SimpleHandler {

	private Twitter bansTwitter, defaultTwitter;

	public TwitterManager(ProxyManager manager) {
		super(manager);
	}

	public boolean initialize() {
		this.bansTwitter = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
				.setOAuthConsumerKey(TwitterAccount.ZENIX_BANS.getConsumerKey())
				.setOAuthConsumerSecret(TwitterAccount.ZENIX_BANS.getConsumerSecret())
				.setOAuthAccessToken(TwitterAccount.ZENIX_BANS.getAccessToken())
				.setOAuthAccessTokenSecret(TwitterAccount.ZENIX_BANS.getAccessSecret()).build()).getInstance();

		this.defaultTwitter = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
				.setOAuthConsumerKey(TwitterAccount.ZENIXCC.getConsumerKey())
				.setOAuthConsumerSecret(TwitterAccount.ZENIXCC.getConsumerSecret())
				.setOAuthAccessToken(TwitterAccount.ZENIXCC.getAccessToken())
				.setOAuthAccessTokenSecret(TwitterAccount.ZENIXCC.getAccessSecret()).build()).getInstance();

		return true;
	}

	public void handleTweet(TwitterAccount twitterAccount, String status) {
		try {
			if (twitterAccount == TwitterAccount.ZENIX_BANS) {
			//	getBansTwitter().updateStatus(status);
			} else if (twitterAccount == TwitterAccount.ZENIXCC) {
			//	getDefaultTwitter().updateStatus(status);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Twitter getBansTwitter() {
		return bansTwitter;
	}

	public Twitter getDefaultTwitter() {
		return defaultTwitter;
	}

}

package br.com.zenix.core.spigot.twitter;

import org.bukkit.Bukkit;

import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager extends Management {

	private Twitter bansTwitter, defaultTwitter; 
	
	public TwitterManager(CoreManager manager) {
		super(manager);
	}

	public boolean initialize() {

		this.bansTwitter = new TwitterFactory(
				new ConfigurationBuilder().setDebugEnabled(true)
				.setOAuthConsumerKey(TwitterAccount.ZENIX_BANS.getConsumerKey())
				.setOAuthConsumerSecret(TwitterAccount.ZENIX_BANS.getConsumerSecret())
				.setOAuthAccessToken(TwitterAccount.ZENIX_BANS.getAccessToken())
				.setOAuthAccessTokenSecret(TwitterAccount.ZENIX_BANS.getAccessSecret())
				.build()).getInstance();

		this.defaultTwitter = new TwitterFactory(
				new ConfigurationBuilder()
				.setDebugEnabled(true)
				.setOAuthConsumerKey(TwitterAccount.ZENIXCC.getConsumerKey())
				.setOAuthConsumerSecret(TwitterAccount.ZENIXCC.getConsumerSecret())
				.setOAuthAccessToken(TwitterAccount.ZENIXCC.getAccessToken())
				.setOAuthAccessTokenSecret(TwitterAccount.ZENIXCC.getAccessSecret())
				.build()).getInstance();
		
		return true;
	}
	
	public void handleTweet(TwitterAccount twitterAccount, String status){
		Bukkit.getScheduler().runTaskAsynchronously(getCoreManager().getPlugin(), new TweetAsyncRunnable(twitterAccount, status));
	}

	public final class TweetAsyncRunnable implements Runnable {

		private final TwitterAccount twitterAccount;
		private final String status;

		public TweetAsyncRunnable(TwitterAccount twitterAccount, String status) {
			this.twitterAccount = twitterAccount;
			this.status = status;
		}

		public void run() {
			try {
				if (twitterAccount == TwitterAccount.ZENIX_BANS) {
					//getBansTwitter().updateStatus(status);
				} else if (twitterAccount == TwitterAccount.ZENIXCC) {
					getDefaultTwitter().updateStatus(status);
				}
			} catch (Exception exception) {
				getLogger().error("The plugin found a error trying to update a twitter status." , exception);
			}
		}
	}
	
	public Twitter getBansTwitter() {
		return bansTwitter;
	}
	
	public Twitter getDefaultTwitter() {
		return defaultTwitter;
	}

}

package br.com.zenix.core.spigot.world;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.PagableResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
 
/**
 * Classe criada em 20/07/17.
 * Desenvolvido por:
 *
 * @author Luãn Pereira.
 */
public class FriendDay {
 
    private Twitter twitter;
 
    public static void main(String[] args) {
        new FriendDay().start();
    }
 
    public FriendDay() {
        String consumerKey = "kP2YWcyXSoMlu0u8JxLoNJ8fj";
        String consumerSecret = "FUn6WuLoJFUfdvWZ8P5uZwpPgNufymaM2A2VtRmraKwfuIpKgL";
 
        String accessToken = "824309197638664198-LdGjmV86j5xkiCbqzfEXk2kAi3jbygY";
        String accessTokenSecret = "4D9byxsKe6kXXWy1yzEYGYsPRY7rqsMSNjwRkFh4gcxt3";
 
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        twitter = new TwitterFactory(cb.build()).getInstance();
    }
 
    public void start() {
        try {
            List<String> friends = getFriends();
 
            int index = 0;
            Map<Integer, String> tweets = new HashMap<>();
            tweets.put(0, "Feliz dia do amigo!");
 
            for (int i = 0; i < friends.size(); i++) {
                String text = tweets.computeIfAbsent(index, v -> "");
                String add = (!text.isEmpty() ? " " : "") + "@" + friends.get(i) + ",";
                if (text.length() + add.length() > 280) {
                    if (text.endsWith(",")) {
                        text = text.substring(0, text.length()-1);
                        tweets.put(index, text);
                    }
                    i--;
                    index++;
                    continue;
                }
                text += add;
                if (i == friends.size()-1) {
                    if (text.endsWith(","))
                        text = text.substring(0, text.length()-1);
                }
                tweets.put(index, text);
            }
 
            tweets.put(tweets.size(), "Amo vocês <3\n\nTweet automático\nEsse programa é do @luanpereiradev");
 
            Status status = Tweet(tweets.get(0));
            for (int i = 1; i < tweets.size(); i++) {
                Tweet(status, tweets.get(i));
                if (i % 5 == 0) {
                    Thread.sleep(5000L);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public Status Tweet(String text) throws TwitterException {
        return Tweet(null, text);
    }
 
    public Status Tweet(Status status, String text) throws TwitterException {
        if (status != null) {
            return twitter.updateStatus(new StatusUpdate(text).inReplyToStatusId(status.getId()));
        } else {
            return twitter.updateStatus(text);
        }
    }
 
    public List<String> getFriends() throws TwitterException {
        long cursor = -1;
        List<String> friends = new ArrayList<>();
        PagableResponseList<User> pagableFollowings;
        do {
            pagableFollowings = twitter.getFriendsList(twitter.getId(), cursor);
            for (User user : pagableFollowings) {
                friends.add(user.getScreenName());
            }
        } while ((cursor = pagableFollowings.getNextCursor()) != 0);
        return friends;
    }
 
}
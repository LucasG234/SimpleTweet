package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {
    // @Embedded notation flattens the properties of the User object into the object, preserving encapsulation.
    @Embedded
    User user;

    @Embedded(prefix = "tweet_")
    Tweet tweet;


    public static List<Tweet> getTweetList(List<TweetWithUser> list) {
        List<Tweet> tweets = new ArrayList<>();
        // Take each TweetWithUser object from the list and return the Tweet objects contained within them with the User reference fixed
        for (int i = 0; i < list.size(); i++) {
            Tweet tweet = list.get(i).tweet;
            tweet.setUser(list.get(i).user);
            tweets.add(tweet);
        }
        return tweets;
    }
}

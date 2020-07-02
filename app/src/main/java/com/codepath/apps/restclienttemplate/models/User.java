package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {

    // Keys in the Twitter response JSON
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String SCREEN_NAME_KEY = "screen_name";
    private static final String IMAGE_URL_KEY = "profile_image_url_https";

    // All members are non-private for Parceler
    @ColumnInfo
    @PrimaryKey
    long id;
    @ColumnInfo
    String name;
    @ColumnInfo
    String screenName;
    @ColumnInfo
    String imageUrl;

    // Empty constructor needed for Parceler
    public User() {}

    private User(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getLong(ID_KEY);
        this.name = jsonObject.getString(NAME_KEY);
        this.screenName = jsonObject.getString(SCREEN_NAME_KEY);
        this.imageUrl = jsonObject.getString(IMAGE_URL_KEY);
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        return new User(jsonObject);
    }

    // Extract the User references from a List of Tweets
    public static List<User> fromJsonTweetArray(List<Tweet> tweets) {
        List<User> users = new ArrayList<>();

        for(int i = 0; i < tweets.size(); i++) {
            users.add(tweets.get(i).getUser());
        }

        return users;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getId() {
        return id;
    }
}

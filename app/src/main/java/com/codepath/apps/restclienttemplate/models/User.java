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

    // Keys from Twitter response JSON
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String SCREEN_NAME_KEY = "screen_name";
    private static final String IMAGE_URL_KEY = "profile_image_url_https";

    // Non-private members and empty constructor for Parceler
    @ColumnInfo
    @PrimaryKey
    long id;
    @ColumnInfo
    String name;
    @ColumnInfo
    String screenName;
    @ColumnInfo
    String imageUrl;


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

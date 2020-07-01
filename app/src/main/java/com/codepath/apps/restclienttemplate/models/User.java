package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    // Keys from Twitter response JSON
    private static final String NAME_KEY = "name";
    private static final String SCREEN_NAME_KEY = "screen_name";
    private static final String IMAGE_URL_KEY = "profile_image_url_https";

    // Non-private members and empty constructor for Parceler
    String name;
    String screenName;
    String imageUrl;
    public User() {}

    private User(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString(NAME_KEY);
        this.screenName = jsonObject.getString(SCREEN_NAME_KEY);
        this.imageUrl = jsonObject.getString(IMAGE_URL_KEY);
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        return new User(jsonObject);
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
}

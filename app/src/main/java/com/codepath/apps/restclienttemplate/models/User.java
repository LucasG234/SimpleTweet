package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    String name;
    String screenName;
    String imageUrl;

    private User(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.screenName = jsonObject.getString("screen_name");
        this.imageUrl = jsonObject.getString("profile_image_url_https");
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

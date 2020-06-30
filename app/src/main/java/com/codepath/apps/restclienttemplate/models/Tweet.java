package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    // Keys from Twitter response JSON
    public static final String BODY_KEY = "text";
    public static final String CREATED_AT_KEY = "created_at";
    public static final String USER_KEY = "user";
    public static final String ENTITIES_OBJECT_KEY = "entities";
    public static final String MEDIA_ARRAY_KEY = "media";
    public static final String MEDIA_DISPLAY_URL_KEY = "media_url_https";

    private static final String TAG = "Tweet";

    // Non-private members and empty constructor for Parceler
    String body;
    String createdAt;
    //TODO: display multiple images
    String mediaDisplayUrl;
    User user;

    public Tweet() {}

    private Tweet(JSONObject jsonObject) throws JSONException {
        this.body = jsonObject.getString(BODY_KEY);
        this.createdAt = jsonObject.getString(CREATED_AT_KEY);
        this.user = User.fromJson(jsonObject.getJSONObject(USER_KEY));
        mediaDisplayUrl = null;
        JSONObject entities = jsonObject.getJSONObject(ENTITIES_OBJECT_KEY);
        // If the object has the media array and its not null, store it to be rendered
        if(entities.has("media")) {
            JSONArray mediaArray = entities.getJSONArray(MEDIA_ARRAY_KEY);
            mediaDisplayUrl = mediaArray.getJSONObject(0).getString(MEDIA_DISPLAY_URL_KEY);
            Log.i(TAG, "successfully collected media URL: " + mediaDisplayUrl);
        }
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        return new Tweet(jsonObject);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException{
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaDisplayUrl() {
        return mediaDisplayUrl;
    }
}

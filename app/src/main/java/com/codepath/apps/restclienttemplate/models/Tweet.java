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

    private static final String TAG = "Tweet";

    // Non-private members and empty constructor for Parceler
    String body;
    String createdAt;
    //TODO: display multiple images
    String mediaDisplayUrl;
    User user;

    public Tweet() {}

    private Tweet(JSONObject jsonObject) throws JSONException {
        this.body = jsonObject.getString("text");
        this.createdAt = jsonObject.getString("created_at");
        this.user = User.fromJson(jsonObject.getJSONObject("user"));
        mediaDisplayUrl = null;
        JSONObject entities = jsonObject.getJSONObject("entities");
        // If the object has the media array and its not null, store it to be rendered
        if(entities.has("media")) {
            JSONArray mediaArray = entities.getJSONArray("media");
            mediaDisplayUrl = mediaArray.getJSONObject(0).getString("media_url_https");
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

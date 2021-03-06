package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {

    // Keys in the Twitter response JSON
    private static final String BODY_KEY = "text";
    private static final String CREATED_AT_KEY = "created_at";
    private static final String ID_KEY = "id";
    private static final String USER_KEY = "user";
    private static final String ENTITIES_OBJECT_KEY = "entities";
    private static final String MEDIA_ARRAY_KEY = "media";
    private static final String MEDIA_DISPLAY_URL_KEY = "media_url_https";
    private static final String LIKED_KEY = "favorited";
    private static final String RETWEETED_KEY = "retweeted";

    public static final int MAX_TWEET_LENGTH = 280;

    private static final String TAG = "Tweet";

    // All members are non-private for Parceler
    @ColumnInfo
    @PrimaryKey
    long id;
    @ColumnInfo
    String body;
    @ColumnInfo
    String createdAt;
    @ColumnInfo
    boolean liked;
    @ColumnInfo
    boolean retweeted;
    @ColumnInfo
    String mediaDisplayUrl;
    @ColumnInfo
    long userId;
    @Ignore
    User user;

    // Empty constructor needed for Parceler
    public Tweet() {
    }

    private Tweet(JSONObject jsonObject) throws JSONException {
        this.liked = false;
        this.retweeted = false;

        this.body = jsonObject.getString(BODY_KEY);
        this.createdAt = jsonObject.getString(CREATED_AT_KEY);
        this.id = jsonObject.getLong(ID_KEY);
        this.liked = jsonObject.getBoolean(LIKED_KEY);
        this.retweeted = jsonObject.getBoolean(RETWEETED_KEY);
        this.user = User.fromJson(jsonObject.getJSONObject(USER_KEY));
        this.userId = user.getId();

        this.mediaDisplayUrl = null;
        JSONObject entities = jsonObject.getJSONObject(ENTITIES_OBJECT_KEY);
        // If the object has the media array and its not null, store the media url to be rendered
        if (entities.has("media")) {
            JSONArray mediaArray = entities.getJSONArray(MEDIA_ARRAY_KEY);
            mediaDisplayUrl = mediaArray.getJSONObject(0).getString(MEDIA_DISPLAY_URL_KEY);
            Log.i(TAG, "successfully collected media URL: " + mediaDisplayUrl);
        }
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        return new Tweet(jsonObject);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
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

    public void setUser(User user) {
        this.user = user;
    }

    public String getMediaDisplayUrl() {
        return mediaDisplayUrl;
    }

    public long getId() {
        return id;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }
}

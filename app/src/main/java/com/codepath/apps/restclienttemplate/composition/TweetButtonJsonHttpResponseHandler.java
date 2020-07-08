package com.codepath.apps.restclienttemplate.composition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetButtonJsonHttpResponseHandler extends JsonHttpResponseHandler {


    private static final String TAG = "TweetyButtonJsonHttpRes";

    private Activity mParent;

    public TweetButtonJsonHttpResponseHandler(Activity parent) {
        mParent = parent;
    }

    @Override
    public void onSuccess(int statusCode, Headers headers, JSON json) {
        Log.i(TAG, "success publishing tweet");
        try {
            Tweet tweet = Tweet.fromJson(json.jsonObject);
            Log.i(TAG, "Published tweet says: " + tweet.getBody());

            // Return information to timeline
            Intent returnToTimelineIntent = new Intent();
            returnToTimelineIntent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            mParent.setResult(mParent.RESULT_OK, returnToTimelineIntent);
            mParent.finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
        Log.e(TAG, "http failure on posting tweet: " + response, throwable);
        Toast.makeText(mParent, mParent.getString(R.string.post_failure), Toast.LENGTH_SHORT).show();
    }
}

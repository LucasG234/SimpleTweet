package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final int COMPOSE_REQUEST_CODE = 67;

    private static final String TAG = "ComposeActivity";

    private TwitterClient mClient;

    private EditText mComposeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComposeBinding composeBinding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(composeBinding.getRoot());

        mClient = TwitterApp.getRestClient(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.compose_label));

        TextInputLayout mComposeTextLayout = composeBinding.layoutEtCompose;
        mComposeText = composeBinding.etCompose;
        Button mComposeButton = composeBinding.btnCompose;

        mComposeTextLayout.setCounterMaxLength(MAX_TWEET_LENGTH);

        // Set click listener on the reply button to make an API call to twitter
        mComposeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = mComposeText.getText().toString();
                if (tweetText.length() == 0) {
                    Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_empty), Toast.LENGTH_SHORT).show();
                } else if (tweetText.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_long), Toast.LENGTH_SHORT).show();
                } else {
                    mClient.publishTweet(tweetText, new ComposeButtonJsonResponseHandler());
                }
            }
        });
    }

    class ComposeButtonJsonResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, "success publishing tweet");
            try {
                Tweet tweet = Tweet.fromJson(json.jsonObject);
                Log.i(TAG, "Published tweet says: " + tweet.getBody());

                // Return information to timeline
                Intent returnToTimelineIntent = new Intent();
                returnToTimelineIntent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                setResult(RESULT_OK, returnToTimelineIntent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.e(TAG, "FAILURE: " + response, throwable);
        }
    }
}
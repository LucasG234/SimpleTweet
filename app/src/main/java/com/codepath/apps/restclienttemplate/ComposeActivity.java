package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final int COMPOSE_REQUEST_CODE = 67;

    private static final String TAG = "ComposeActivity";

    private TwitterClient mClient;

    private EditText mComposeText;
    private Button mComposeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        mClient = TwitterApp.getRestClient(this);

        mComposeText = findViewById(R.id.etCompose);
        mComposeButton = findViewById(R.id.btnCompose);

        // Set click listener on the compose button to make an API call to twitter
        mComposeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = mComposeText.getText().toString();
                if(tweetText.length() == 0) {
                    Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_empty), Toast.LENGTH_SHORT).show();
                }
                else if(tweetText.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_long), Toast.LENGTH_SHORT).show();
                }
                else {
                    mClient.publishTweet(tweetText, new JsonHttpResponseHandler() {
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
                    });
                }
            }
        });
    }
}
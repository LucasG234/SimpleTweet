package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityReplyBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final int REPLY_REQUEST_CODE = 68;

    private static final String TAG = "ComposeActivity";

    private TwitterClient mClient;

    private EditText mComposeText;
    private TextView mReplyName;
    private Button mComposeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReplyBinding replyBinding = ActivityReplyBinding.inflate(getLayoutInflater());
        setContentView(replyBinding.getRoot());

        mClient = TwitterApp.getRestClient(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.reply_label));

        TextInputLayout mComposeTextLayout = replyBinding.layoutEtCompose;
        mComposeText = replyBinding.etCompose;
        mComposeButton = replyBinding.btnCompose;
        mReplyName = replyBinding.tvReplyName;

        mComposeTextLayout.setCounterMaxLength(MAX_TWEET_LENGTH);

        Tweet parentTweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        final Long replyId = parentTweet.getId();

        mReplyName.setText(getString(R.string.reply_name) + parentTweet.getUser().getScreenName());

        // Set click listener on the compose button to make an API call to twitter
        mComposeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = mComposeText.getText().toString();
                if(tweetText.length() == 0) {
                    Toast.makeText(ReplyActivity.this, getString(R.string.compose_error_empty), Toast.LENGTH_SHORT).show();
                }
                else if(tweetText.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ReplyActivity.this, getString(R.string.compose_error_long), Toast.LENGTH_SHORT).show();
                }
                else {
                    mClient.publishReply(tweetText, replyId, new JsonHttpResponseHandler() {
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
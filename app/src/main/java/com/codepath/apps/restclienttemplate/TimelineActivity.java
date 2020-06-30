package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity";

    private List<Tweet> mTweets;
    private TwitterClient mClient;
    private RecyclerView twitterRecycler;
    private TwitterAdapter twitterAdapter;
    private SwipeRefreshLayout mSwipeTimelineLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mClient = TwitterApp.getRestClient(this);

        mSwipeTimelineLayout = findViewById(R.id.layoutRvTimeline);
        mSwipeTimelineLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                populateHomeTimeline();
            }
        });

        twitterRecycler = findViewById(R.id.rvTimeline);
        mTweets = new ArrayList<>();
        twitterAdapter = new TwitterAdapter(this, mTweets);

        twitterRecycler.setAdapter(twitterAdapter);
        twitterRecycler.setLayoutManager(new LinearLayoutManager(this));

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success populating timeline ");
                JSONArray jsonArray = json.jsonArray;
                try {
                    twitterAdapter.clear();
                    twitterAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    // Refresh is done now, so remove the loading icon
                    mSwipeTimelineLayout.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "FAILURE: " + response, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuCompose) {
            // Create compose activity
            Intent composeIntent = new Intent(this, ComposeActivity.class);
            startActivityForResult(composeIntent, ComposeActivity.COMPOSE_REQUEST_CODE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ComposeActivity.COMPOSE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get data from published tweet and update the timleine
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            mTweets.add(0, tweet);
            twitterAdapter.notifyItemInserted(0);
            twitterRecycler.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
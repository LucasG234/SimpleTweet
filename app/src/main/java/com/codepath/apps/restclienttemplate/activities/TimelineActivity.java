package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
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
    private RecyclerView mTwitterRecycler;
    private TwitterAdapter mTwitterAdapter;
    private SwipeRefreshLayout mSwipeTimelineLayout;
    private TweetDao mTweetDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTimelineBinding timelineBinding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(timelineBinding.getRoot());

        mClient = TwitterApp.getRestClient(this);
        mTweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.timeline_label));

        // This layout allows swipe to refresh
        mSwipeTimelineLayout = timelineBinding.layoutRvTimeline;
        configureSwipeTimelineLayout(mSwipeTimelineLayout);

        // Configure Recycler to hold the timeline and use infinite scroll
        mTwitterRecycler = timelineBinding.rvTimeline;
        mTweets = new ArrayList<>();
        mTwitterAdapter = new TwitterAdapter(this, mClient, mTweets);
        mTwitterRecycler.setAdapter(mTwitterAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mTwitterRecycler.setLayoutManager(linearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreData();
            }
        };
        mTwitterRecycler.addOnScrollListener(scrollListener);

        // Query for existing tweets in the Data Base
        // These tweets will be overwritten if our request to the Twitter API is successful
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Loading data from the DB");
                List<TweetWithUser> tweetWithUsers = mTweetDao.recentItems();
                mTwitterAdapter.clear();
                mTwitterAdapter.addAll(TweetWithUser.getTweetList(tweetWithUsers));
            }
        });
        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success populating timeline ");
                JSONArray jsonArray = json.jsonArray;
                try {
                    // Clear out the page (if it already had anything) and refresh it
                    mTwitterAdapter.clear();
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    mTwitterAdapter.addAll(tweetsFromNetwork);

                    // Add the newly found data to the Data Base
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Saving data into the DB");
                            // Insert Users first because of foreign key references in the Tweets
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            mTweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            mTweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });

                    // Refresh is done now, so remove the loading icon
                    mSwipeTimelineLayout.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception on population", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "FAILURE: " + response, throwable);
                // Refresh is done now, so remove the loading icon
                mSwipeTimelineLayout.setRefreshing(false);
            }
        });
    }

    private void loadMoreData() {
        // Give getNextPageofTweets the ID of the oldest tweet
        mClient.getNextPageOfTweets(mTweets.get(mTweets.size()-1).getId(),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "success on loadmoredata");

                        JSONArray jsonArray = json.jsonArray;
                        try {
                            mTwitterAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                        } catch (JSONException e) {
                            Log.e(TAG, "Json exception on loadmoredata", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure for loadMoreData: " + response, throwable);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Change the color of the menu icon
        Drawable drawable = menu.getItem(0).getIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_ATOP);
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
        if ((requestCode == ComposeActivity.COMPOSE_REQUEST_CODE || requestCode == ReplyActivity.REPLY_REQUEST_CODE)
                && resultCode == RESULT_OK) {
            // Get data from published tweet and update the timeline
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            mTweets.add(0, tweet);
            mTwitterAdapter.notifyItemInserted(0);
            mTwitterRecycler.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void configureSwipeTimelineLayout(SwipeRefreshLayout mSwipeTimelineLayout) {
        // Add the listener
        mSwipeTimelineLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "fetching new data");
                populateHomeTimeline();
            }
        });

        // Configure the refreshing colors
        mSwipeTimelineLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.activities.ReplyActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.TwitterViewHolder> {

    private static final String TAG = "TwitterAdapter";

    // TimelineActivty is the only context allowed for this class
    private TimelineActivity mParentView;
    private TwitterClient mClient;
    private List<Tweet> mTweets;

    public TwitterAdapter(TimelineActivity context, TwitterClient client, List<Tweet> tweets) {
        this.mParentView = context;
        this.mClient = client;
        this.mTweets = tweets;
    }

    @NonNull
    @Override
    public TwitterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTweetBinding tweetBinding = ItemTweetBinding.inflate(LayoutInflater.from(mParentView), parent, false);
        return new TwitterViewHolder(tweetBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TwitterViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Method to clear all elements from the RecyclerView
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Method to add a list of items to the RecyclerView
    public void addAll(List<Tweet> tweets) {
        mTweets.addAll(tweets);
        notifyDataSetChanged();
    }

    public class TwitterViewHolder extends RecyclerView.ViewHolder {

        private ItemTweetBinding tweetBinding;

        public TwitterViewHolder(@NonNull ItemTweetBinding binding) {
            super(binding.getRoot());
            tweetBinding = binding;
        }

        public void bind(Tweet tweet) {
            tweetBinding.tvTweetBody.setText(tweet.getBody());
            tweetBinding.tvScreenName.setText("@" + tweet.getUser().getScreenName());
            Glide.with(mParentView)
                    .load(tweet.getUser().getImageUrl())
                    .transform(new CircleCrop())
                    .into(tweetBinding.ivProfileImage);

            setTweetMedia(tweetBinding.ivTweetMedia, tweet);
            configureButtons(tweetBinding, tweet);
        }

        private void setTweetMedia(ImageView tweetMedia, Tweet tweet) {
            if(tweet.getMediaDisplayUrl() != null) {
                tweetMedia.setVisibility(View.VISIBLE);
                Glide.with(mParentView)
                        .load(tweet.getMediaDisplayUrl())
                        .into(tweetMedia);
            }
            else {
                tweetMedia.setVisibility(View.GONE);
            }
        }

        private void configureButtons(ItemTweetBinding tweetBinding, final Tweet tweet) {
            final ImageView likeButton = tweetBinding.ivLike;
            final ImageView replyButton = tweetBinding.ivReply;
            final ImageView retweetButton = tweetBinding.ivRetweet;

            // Change look of like and retweet buttons initially if the tweet is already either
            if(tweet.isLiked()) {
                Glide.with(mParentView)
                        .load(R.drawable.ic_vector_heart)
                        .into(likeButton);
                ImageViewCompat.setImageTintList(likeButton, ColorStateList.valueOf(ContextCompat.getColor(mParentView, R.color.medium_red)));
            }
            if(tweet.isRetweeted()) {
                ImageViewCompat.setImageTintList(retweetButton, ColorStateList.valueOf(ContextCompat.getColor(mParentView, R.color.medium_green)));
            }
            // Set onClickListeners for all 3 buttons
            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start a new compose activity for replying
                    // Result will be sent to parent TimelineActivity
                    Intent replyIntent = new Intent(mParentView, ReplyActivity.class);
                    // Put the parent tweet into the Intent to be used
                    replyIntent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    mParentView.startActivityForResult(replyIntent, ReplyActivity.REPLY_REQUEST_CODE);
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClient.likeUnlikeTweet(tweet.getId(), !tweet.isLiked(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            int newPhotoId = tweet.isLiked() ? R.drawable.ic_vector_heart_stroke : R.drawable.ic_vector_heart;
                            Log.i(TAG, "successfully [un]liked tweet");
                            // Change the like button visually: switch tinting and image
                            Glide.with(mParentView)
                                    .load(newPhotoId)
                                    .into(likeButton);
                            int colorToSet = tweet.isLiked() ?
                                    ContextCompat.getColor(mParentView, R.color.light_gray) : ContextCompat.getColor(mParentView, R.color.medium_red);
                            ImageViewCompat.setImageTintList(likeButton, ColorStateList.valueOf(colorToSet));

                            tweet.setLiked(!tweet.isLiked());
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            String toastText = tweet.isLiked() ? mParentView.getString(R.string.like_failure) : mParentView.getString(R.string.unlike_failure);
                            Toast.makeText(mParentView, toastText, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "failure to [un]like tweet: " + response, throwable);
                        }
                    });

                }
            });

            retweetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClient.retweetUnretweetTweet(tweet.getId(), !tweet.isRetweeted(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "[un]retweet successful");
                            // Change the retweet button visually: switch tinting
                            int colorToSet = tweet.isRetweeted() ?
                                    ContextCompat.getColor(mParentView, R.color.light_gray) : ContextCompat.getColor(mParentView, R.color.medium_green);
                            ImageViewCompat.setImageTintList(retweetButton, ColorStateList.valueOf(colorToSet));
                            tweet.setRetweeted(!tweet.isRetweeted());
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "[un]retweet failed: " + response, throwable);
                        }
                    });
                }
            });
        }
    }

}

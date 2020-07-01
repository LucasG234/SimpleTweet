package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.activities.ReplyActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.TwitterViewHolder> {

    // TimelineActivty is the only context allowed for this class
    private TimelineActivity mParentView;
    private List<Tweet> mTweets;

    public TwitterAdapter(TimelineActivity context, List<Tweet> tweets) {
        this.mParentView = context;
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
            //TODO= change names
            ImageView likeButton = tweetBinding.ivLike;
            ImageView replyButton = tweetBinding.ivReply;
            ImageView retweetButton = tweetBinding.ivRetweet;

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

                }
            });
        }
    }
}

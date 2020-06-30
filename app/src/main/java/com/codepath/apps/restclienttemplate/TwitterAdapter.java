package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.TwitterViewHolder> {

    private Context mContext;
    private List<Tweet> mTweets;

    public TwitterAdapter(Context context, List<Tweet> tweets) {
        this.mContext = context;
        this.mTweets = tweets;
    }

    @NonNull
    @Override
    public TwitterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tweet, parent, false);
        return new TwitterViewHolder(view);
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

        private ImageView mProfileImage;
        private TextView mScreenName;
        private TextView mTweetBody;

        public TwitterViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.ivProfileImage);
            mScreenName = itemView.findViewById(R.id.tvScreenName);
            mTweetBody = itemView.findViewById(R.id.tvTweetBody);

        }

        public void bind(Tweet tweet) {
            mTweetBody.setText(tweet.getBody());
            mScreenName.setText(tweet.getUser().getScreenName());
            Glide.with(mContext)
                    .load(tweet.getUser().getImageUrl())
                    .into(mProfileImage);
        }
    }
}

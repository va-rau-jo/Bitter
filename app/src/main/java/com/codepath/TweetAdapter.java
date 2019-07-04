package com.codepath;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.activities.ComposeActivity;
import com.codepath.models.Tweet;
import com.codepath.utils.FormatHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private ArrayList<Tweet> tweets;
    private Context context;

    public TweetAdapter(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    /**
     * Method called when the adapter has to display the data on a view
     * @param viewGroup The parent view
     * @param i
     * @return
     */
    @NonNull
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View tweetView = layoutInflater.inflate(R.layout.item_tweet, viewGroup, false);
        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetAdapter.ViewHolder viewHolder, int i) {
        Tweet tweet = tweets.get(i);
        viewHolder.tvUsername.setText(tweet.user.username);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvTimestamp.setText(FormatHelper.getRelativeTimeAgo(tweet.createdAt));
        viewHolder.tvRetweetCounter.setText(FormatHelper.formatCounter(tweet.retweets));
        viewHolder.tvFavoriteCounter.setText(FormatHelper.formatCounter(tweet.favorites));

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    /**
     * Helper method to clear the list of tweets, so we can refresh
     */
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    /**
     * Helper method to add all the tweets back to the list adapter
     * @param list the list of new tweets
     */
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.tvUsername)
        TextView tvUsername;

        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;

        @BindView(R.id.tvRetweetCounter)
        TextView tvRetweetCounter;

        @BindView(R.id.tvFavoriteCounter)
        TextView tvFavoriteCounter;

        @BindView(R.id.ibReply)
        ImageButton ibReply;

        @BindView(R.id.ibRetweet)
        ImageButton ibRetweet;

        @BindView(R.id.ibFavorite)
        ImageButton ibFavorite;

        @BindView(R.id.ibDM)
        ImageButton ibDM;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ibReply)
        public void setReplyOnClick(View v) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                String userTag = "@" + tweet.user.username + " ";
                Intent intent = new Intent(context, ComposeActivity.class);
                intent.putExtra("reply_user_tag", userTag);
                intent.putExtra("reply_tweet_id", tweet.uid);
                context.startActivity(intent);
            }
        }

        @OnClick(R.id.ibRetweet)
        public void setRetweetOnClick(ImageButton button) {
            Object tag = button.getTag();

            if(tag == null) {
                button.setTag("clicked");
                button.setImageResource(R.drawable.ic_vector_retweet);
            } else {
                button.setTag(null);
                button.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }
        }

        @OnClick(R.id.ibFavorite)
        public void setFavoriteOnClick() {

        }

        @OnClick(R.id.ibDM)
        public void setDMOnClick() {

        }
    }

}

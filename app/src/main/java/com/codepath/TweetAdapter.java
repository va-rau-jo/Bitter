package com.codepath;

import android.app.Activity;
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
import com.codepath.activities.DetailActivity;
import com.codepath.models.Tweet;
import com.codepath.utils.FormatHelper;
import com.codepath.utils.ResultCodes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private ArrayList<Tweet> tweets;
    private Context context;
    private Activity activity;

    public TweetAdapter(Activity context, ArrayList<Tweet> tweets) {
        this.tweets = tweets;
        this.activity = context;
    }

    /**
     * Method called when the adapter has to display the data on a view, creates one view holder
     * @param viewGroup The parent view
     * @param viewType Type of the parent view, doesn't matter here
     * @return The created view holder
     */
    @NonNull
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View tweetView = layoutInflater.inflate(R.layout.item_tweet, viewGroup, false);
        return new ViewHolder(tweetView);
    }

    /**
     * Method called to bind data to an already created view holder
     * @param viewHolder The already created view holder that will be populated
     * @param index The index of the view holder
     */
    @Override
    public void onBindViewHolder(@NonNull TweetAdapter.ViewHolder viewHolder, int index) {
        Tweet tweet = tweets.get(index);
        viewHolder.tvUsername.setText(tweet.user.username);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvTimestamp.setText(FormatHelper.getRelativeTimeAgo(tweet.createdAt));

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(viewHolder.ivProfileImage);

        if(tweet.hasEntities) {
            viewHolder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.entity.mediaURL)
                    .bitmapTransform(new RoundedCornersTransformation(context, 10, 0))
                    .into(viewHolder.ivMedia);
        } else {
            viewHolder.ivMedia.setVisibility(View.GONE);
        }
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

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.tvUsername)
        TextView tvUsername;

        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;

        @BindView(R.id.ibReply)
        ImageButton ibReply;

        @BindView(R.id.ivMedia)
        ImageView ivMedia;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("tweet", tweets.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }

        @OnClick(R.id.ibReply)
        void setReplyOnClick() {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                String userTag = "@" + tweet.user.username + " ";
                Intent intent = new Intent(context, ComposeActivity.class);
                intent.putExtra("reply_user_tag", userTag);
                intent.putExtra("reply_tweet_id", tweet.uid);
                activity.startActivityForResult(intent, ResultCodes.COMPOSE_RESULT_CODE);
            }
        }
    }
}

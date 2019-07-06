package com.codepath.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.BitterClient;
import com.codepath.R;
import com.codepath.models.Tweet;
import com.codepath.utils.FormatHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailActivity extends AppCompatActivity {
    private Tweet tweet;
    private BitterClient client;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.tvUsername)
    TextView tvUsername;

    @BindView(R.id.tvBody)
    TextView tvBody;

    @BindView(R.id.tvTimestamp)
    TextView tvTimestamp;

    @BindView(R.id.ibRetweet)
    ImageButton ibRetweet;

    @BindView(R.id.ibFavorite)
    ImageButton ibFavorite;

    @BindView(R.id.ibDM)
    ImageButton ibDM;

    @BindView(R.id.tvRetweetCounter)
    TextView tvRetweetCounter;

    @BindView(R.id.tvFavoriteCounter)
    TextView tvFavoriteCounter;

    @BindView(R.id.ivMedia)
    ImageView ivMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.twitter_blue)));

        if(getIntent().getExtras() != null)
            tweet = getIntent().getExtras().getParcelable("tweet");

        initializeView();
    }

    private void initializeView() {
        tvUsername.setText(tweet.user.username);
        tvBody.setText(tweet.body);
        tvTimestamp.setText(FormatHelper.getRelativeTimeAgo(tweet.createdAt));

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(ivProfileImage);

        ibRetweet.setSelected(tweet.retweeted);
        ibFavorite.setSelected(tweet.favorited);

        tvRetweetCounter.setText(FormatHelper.formatCounter(tweet.retweets));
        tvFavoriteCounter.setText(FormatHelper.formatCounter(tweet.favorites));

        if(tweet.hasEntities) {
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet.entity.mediaURL)
                    .bitmapTransform(new RoundedCornersTransformation(this, 10, 0))
                    .into(ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
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
    public void setFavoriteOnClick(final ImageButton button) {
        boolean liked = button.getTag() != null;
        client.favoriteTweet(tweet.uid, !liked, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    throw new JSONException("Shug pu");

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

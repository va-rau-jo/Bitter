package com.codepath.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.R;
import com.codepath.models.Tweet;
import com.codepath.utils.FormatHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailActivity extends AppCompatActivity {
    private Tweet tweet;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

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

        tvRetweetCounter.setText(FormatHelper.formatCounter(tweet.retweets));
        tvFavoriteCounter.setText(FormatHelper.formatCounter(tweet.favorites));
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
}

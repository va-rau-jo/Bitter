package com.codepath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.BitterApp;
import com.codepath.BitterClient;
import com.codepath.R;
import com.codepath.TweetAdapter;
import com.codepath.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // Code for creating a tweet
    public static final int COMPOSE_RESULT_CODE = 11;

    private BitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetAdapter adapter;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        client = BitterApp.getRestClient(this);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateTimeline();
    }

    /**
     * Inflates the timeline and adds the action items to the menu
     * @param menu The menu (specific to this activity/layout)
     * @return true if the options menu was created successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    /**
     * Called to populate the recycler view with the user's timeline
     */
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for(int i = 0; i < response.length(); i++) {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        adapter.notifyItemInserted(tweets.size() - 1);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Callback function that returns the tweet that was uploaded
     * @param requestCode The custom request code for the action (arbitrary)
     * @param resultCode The result code (OK, ERROR, etc)
     * @param data The intent with possible data stored inside
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == COMPOSE_RESULT_CODE &&
            data.getExtras() != null) {
            Tweet tweet = data.getExtras().getParcelable("new_tweet");
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    /**
     * Handles whenever a menu item is clicked
     * @param item The item that is clicked
     * @return true if the item clicked can be handled, false if the item id is unknown
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCompose:
                goToComposeActivity();
                return true;
            default:
                return false;
        }
    }

    /**
     * Starts the compose activity and forces a callback with the new tweet parcel
     */
    private void goToComposeActivity() {
        startActivityForResult(new Intent(this, ComposeActivity.class), COMPOSE_RESULT_CODE);
    }
}

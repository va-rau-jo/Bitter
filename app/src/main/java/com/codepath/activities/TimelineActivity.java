package com.codepath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    public static final int COMPOSE_RESULT_CODE = 11;

    private BitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetAdapter adapter;
    private RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = BitterApp.getRestClient(this);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        rvTweets = findViewById(R.id.rvTweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateTimeline();
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("success", "made it 1");
            }

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
                Log.d("success", "made it 2");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", "uh oh spaghetti-os");
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", "uh oh spaghetti-os");
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", "uh oh spaghetti-os");
                throwable.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == COMPOSE_RESULT_CODE) {
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

    private void goToComposeActivity() {
        startActivityForResult(new Intent(this, ComposeActivity.class), COMPOSE_RESULT_CODE);
    }
}

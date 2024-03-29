package com.codepath.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.BitterApp;
import com.codepath.BitterClient;
import com.codepath.EndlessRecyclerViewScrollListener;
import com.codepath.R;
import com.codepath.TweetAdapter;
import com.codepath.models.Tweet;
import com.codepath.utils.ResultCodes;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private BitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetAdapter adapter;
    private EndlessRecyclerViewScrollListener endlessScrollListener;
    private long maxId = 0;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;

    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;

    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.twitter_blue)));

        client = BitterApp.getRestClient(this);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(TimelineActivity.this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        // Adding endless scrolling
        endlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi();
            }
        };

        rvTweets.addOnScrollListener(endlessScrollListener);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        populateTimeline(maxId);
        return super.onPrepareOptionsMenu(menu);
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

    private void loadNextDataFromApi() {
        maxId = tweets.get(tweets.size() - 1).uid;
        populateTimeline(maxId);
    }

    /**
     * Called to populate the recycler view with the user's timeline
     */
    private void populateTimeline(long maxId) {
        showProgressBar();
        //endlessScrollListener.resetState();
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
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
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                hideProgressBar();
            }
        });
    }

    public void fetchTimelineAsync() {
        showProgressBar();
        //endlessScrollListener.resetState();
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                adapter.clear();
                List<Tweet> newTweets = new ArrayList<>();

                try {
                    for(int i = 0; i < response.length(); i++) {
                        newTweets.add(Tweet.fromJSON(response.getJSONObject(i)));
                    }

                    adapter.addAll(newTweets);
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideProgressBar();
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
        if (resultCode == RESULT_OK && requestCode == ResultCodes.COMPOSE_RESULT_CODE &&
            data.getExtras() != null) {
            Tweet tweet = data.getExtras().getParcelable(getString(R.string.new_tweet_key));
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
        if (item.getItemId() == R.id.miProfile) {
            // Go to profile
            return true;
        } else if(item.getItemId() == R.id.miCompose) {
            startActivity(new Intent(this, ComposeActivity.class));
        }
        return false;
    }

    @OnClick(R.id.fabCompose)
    public void setComposeBtnOnClick() {
        goToComposeActivity();
    }

    private void showProgressBar() {
        miActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        miActionProgressItem.setVisible(false);
    }

    /**
     * Starts the compose activity and forces a callback with the new tweet parcel
     */
    private void goToComposeActivity() {
        startActivityForResult(new Intent(this, ComposeActivity.class), ResultCodes.COMPOSE_RESULT_CODE);
    }
}

package com.codepath.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.BitterApp;
import com.codepath.BitterClient;
import com.codepath.R;
import com.codepath.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private BitterClient client;

    // The id of the tweet you might be replying to
    private Long replyTweetId = (long) -1;

    @BindView(R.id.etTweetInput)
    EditText etTweetInput;

    @BindView(R.id.btnSend)
    Button btnSend;

    @BindView(R.id.tvCharCount)
    TextView tvCharCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
        client = BitterApp.getRestClient(this);

        Bundle bundle = getIntent().getExtras();
        // reply
        if(bundle != null) {
            String userTag = bundle.getString("reply_user_tag");
            replyTweetId = bundle.getLong("reply_tweet_id");
            etTweetInput.setText(userTag);
            etTweetInput.requestFocus();

            // Show keyboard when automatically focused on edit text
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    /**
     * Creates the event listener for a click on the send button. Uses the BitterClient to make a
     * post call to the Twitter API
     */
    @OnClick({R.id.btnSend})
    public void setBtnSendOnClickListener(final Button btnSend) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etTweetInput.getText().toString();
                btnSend.setEnabled(false);

                if(replyTweetId != -1) {
                    client.replyToTweet(message, replyTweetId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                btnSend.setEnabled(true);
                                Tweet newTweet = Tweet.fromJSON(response);
                                Intent intent = new Intent();
                                intent.putExtra(getString(R.string.new_tweet_key), newTweet);
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                // Attempt at uploading a tweet
                client.sendTweet(message, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            btnSend.setEnabled(true);
//                            Tweet newTweet = Tweet.fromJSON(response);
//                            Intent intent = new Intent();
//                            intent.putExtra(getString(R.string.new_tweet_key), newTweet);
//                            setResult(RESULT_OK, intent);
                            finish();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                });
            }
        });
    }

    @OnTextChanged(R.id.etTweetInput)
    public void setOnTextChangedListener() {
        int characters = etTweetInput.getText().toString().length();
        int diff = getResources().getInteger(R.integer.character_count) - characters;

        if(diff < 0) {
            tvCharCount.setTextColor(Color.RED);
            btnSend.setEnabled(false);
        } else {
            tvCharCount.setTextColor(Color.GRAY);
            btnSend.setEnabled(true);
        }
        String charCount = diff + "";
        tvCharCount.setText(charCount);
    }
}

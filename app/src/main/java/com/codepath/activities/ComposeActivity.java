package com.codepath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.BitterApp;
import com.codepath.BitterClient;
import com.codepath.R;
import com.codepath.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private BitterClient client;

    @BindView(R.id.etTweetInput) EditText etTweetInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        client = BitterApp.getRestClient(this);
    }

    /**
     * Creates the event listener for a click on the send button. Uses the BitterClient to make a
     * post call to the Twitter API
     */
    @OnClick({R.id.btnSend})
    public void setBtnSendOnClickListener(Button btnSend) {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.sendTweet(etTweetInput.getText().toString(), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet newTweet = Tweet.fromJSON(response);
                            Intent intent = new Intent();
                            intent.putExtra("new_tweet", newTweet);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("success", "made it 1");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("success", "made it 2");
                    }
                });
            }
        });
    }
}

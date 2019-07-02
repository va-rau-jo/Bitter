package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

    public String body;
    public Long uid;
    public User user;
    public String createdAt;

    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = object.getString("text");
        tweet.uid = object.getLong("id");
        tweet.user = User.fromJSON(object.getJSONObject("user"));
        tweet.createdAt = object.getString("created_at");

        return tweet;
    }
}

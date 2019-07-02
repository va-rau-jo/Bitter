package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    public String name;
    public Long uid;
    public String username;
    public String profileImageUrl;

    public static User fromJSON(JSONObject object) throws JSONException {
        User user = new User();
        user.name = object.getString("name");
        user.uid = object.getLong("id");
        user.username = object.getString("screen_name");
        user.profileImageUrl = object.getString("profile_image_url");

        return user;
    }
}

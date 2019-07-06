package com.codepath.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Entity {

    public String mediaURL;

    public static Entity fromJSON(JSONObject object) throws JSONException {
        Entity entity = new Entity();
        entity.mediaURL = object.getJSONArray("media")
                .getJSONObject(0)
                .getString("media_url_https");
        return entity;
    }}

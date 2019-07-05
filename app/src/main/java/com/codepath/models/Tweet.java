package com.codepath.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet implements Parcelable {

    public String body;
    public Long uid;
    public User user;
    public String createdAt;
    public Integer retweets;
    public Integer favorites;
    public boolean retweeted;
    public boolean favorited;

    private Tweet() {}

    private Tweet(Parcel in) {
        body = in.readString();
        uid = in.readLong();
        user = in.readParcelable(User.class.getClassLoader());
        createdAt = in.readString();
        retweets = in.readInt();
        favorites = in.readInt();
        retweeted = in.readInt() == 1;
        favorited = in.readInt() == 1;
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = object.getString("full_text").replace("&amp;", "&");
        tweet.uid = object.getLong("id");
        tweet.user = User.fromJSON(object.getJSONObject("user"));
        tweet.createdAt = object.getString("created_at");
        tweet.retweets = object.getInt("retweet_count");
        tweet.favorites = object.getInt("favorite_count");
        tweet.retweeted = object.getBoolean("retweeted");
        tweet.favorited = object.getBoolean("favorited");
        return tweet;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeLong(uid);
        dest.writeParcelable(user, 0);
        dest.writeString(createdAt);
        dest.writeInt(retweets);
        dest.writeInt(favorites);
        dest.writeInt(retweeted ? 1 : 0);
        dest.writeInt(favorited ? 1 : 0);
    }
}

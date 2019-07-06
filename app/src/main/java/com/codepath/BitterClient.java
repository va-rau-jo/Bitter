package com.codepath;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class BitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "ABaCtvkJthocDnKfbw8REoOW4";
	public static final String REST_CONSUMER_SECRET = "uVntI345xJ0vTSyExmbTqUvLd2vo8SVznfiLo3tewrsjUodeTa";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";
	private static final int TWEET_AMOUNT = 20;

	public BitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	/**
	 * Get the timeline from the twitter API
	 * @param handler The handler that handles onSuccess and onFailure
	 */
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", TWEET_AMOUNT);
		params.put("tweet_mode", "extended");
		if(maxId != 0)
			params.put("max_id", maxId);

		client.get(apiUrl, params, handler);
	}

	/**
	 * Sends a tweet and uploads it through the Twitter API
	 * @param message The string message sent
	 */
	public void sendTweet(String message, long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", message);
		params.put("tweet_mode", "extended");

		if(tweetId != -1)
			params.put("in_reply_to_status_id", tweetId);

		client.post(apiUrl, params, handler);
	}

	/**
	 * Favorite OR unfavorite a tweet
	 * @param tweetId the ID of a tweet
	 * @param like If true, you are liking the tweet. If false, you are unliking the tweet
	 */
	public void favoriteTweet(long tweetId, boolean like, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/" + (like ? "create" : "destroy") + ".json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		params.put("tweet_mode", "extended");
		client.post(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}

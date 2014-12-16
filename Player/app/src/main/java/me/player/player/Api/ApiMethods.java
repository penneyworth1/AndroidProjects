package me.player.player.Api;

import android.util.Log;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.player.player.AppState;
import me.player.player.Constants.Enums.LoginError;
import me.player.player.Constants.Names;
import me.player.player.Entities.Comment;
import me.player.player.Entities.FeedItem;
import me.player.player.Entities.StreamItem;
import me.player.player.Entities.User;

/**
 * Created by stevenstewart on 9/6/14.
 */
public class ApiMethods
{
    public static void getNewAccessTokens(String username, String password)
    {
        AppState appState = AppState.getInstance();
        appState.loginError = LoginError.NONE;

        ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        parameters.add(new KeyValuePair("grant_type", "password"));
        parameters.add(new KeyValuePair("client_id", Names.API_CLIENT_ID));
        parameters.add(new KeyValuePair("client_secret", Names.API_CLIENT_SECRET));
        parameters.add(new KeyValuePair("username", username));
        parameters.add(new KeyValuePair("password", password));
        ResponseObject responseObject = HttpUtil.makeHttpRequest("oauth/access_token", "POST", null, parameters);

        if(responseObject.responseCode == HttpStatus.SC_OK)
        {
            String accessToken = responseObject.jsonResponse.optString("access_token");
            appState.accessToken = accessToken;
            Log.d("player","accessToken " + accessToken);
            String refreshToken = responseObject.jsonResponse.optString("refresh_token");
            appState.refreshToken = refreshToken;
            Log.d("player","refreshToken " + refreshToken);
        }
        else if(responseObject.responseCode == HttpStatus.SC_BAD_REQUEST)
        {
            appState.accessToken = "";
            appState.refreshToken = "";
            appState.loginError = LoginError.BAD_CREDENTIALS;
        }
        else
        {
            appState.loginError = LoginError.CONNECTION_ERROR;
        }
    }

    public static void getNewAccessTokenWithRefreshToken()
    {
        AppState appState = AppState.getInstance();
        appState.loginError = LoginError.NONE;

        ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        parameters.add(new KeyValuePair("grant_type", "refresh_token"));
        parameters.add(new KeyValuePair("client_id", Names.API_CLIENT_ID));
        parameters.add(new KeyValuePair("client_secret", Names.API_CLIENT_SECRET));
        parameters.add(new KeyValuePair("refresh_token", appState.refreshToken));
        ResponseObject responseObject = HttpUtil.makeHttpRequest("oauth/access_token", "POST", null, parameters);

        if(responseObject.responseCode == HttpStatus.SC_OK)
        {
            String accessToken = responseObject.jsonResponse.optString("access_token");
            appState.accessToken = accessToken;
            Log.d("player","accessToken - gotten view refresh token : " + accessToken);
        }
        else if(responseObject.responseCode == HttpStatus.SC_BAD_REQUEST)
        {
            appState.accessToken = "";
            appState.refreshToken = "";
            appState.loginError = LoginError.BAD_CREDENTIALS;
        }
        else
        {
            appState.loginError = LoginError.CONNECTION_ERROR;
        }
    }

    public static void setMyProfileDescription(String newDescription)
    {
        AppState appState = AppState.getInstance();

        ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Authorization","Bearer " + appState.accessToken));
        ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        parameters.add(new KeyValuePair("description", newDescription));

        ResponseObject responseObject = HttpUtil.makeHttpRequest("v1/users/default","PUT",headers,parameters);

        if(responseObject.responseCode == HttpStatus.SC_OK) { }
        else if(responseObject.responseCode == HttpStatus.SC_BAD_REQUEST) { }
        else { }
    }

    public static boolean createNewPost(String postText, boolean crossPostToFacebook, boolean crossPostToTwitter)
    {
        try
        {
            ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
            headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
            ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
            parameters.add(new KeyValuePair("post", postText));
            if(crossPostToFacebook) parameters.add(new KeyValuePair("facebook", "true"));
            if(crossPostToTwitter) parameters.add(new KeyValuePair("twitter", "true"));

            ResponseObject responseObject = HttpUtil.makeHttpRequest("v1/feed", "POST", headers, parameters);
            boolean success = responseObject.jsonResponse.optBoolean("success");

            return success;
        }
        catch(Exception ex)
        {
            return false;
        }
    }

    public static boolean createNewPostWithImage(String postText, boolean crossPostToFacebook, boolean crossPostToTwitter, String base64encodedImage)
    {
        try
        {
            ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
            headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
            headers.add(new KeyValuePair("Content-type", "application/json"));
            headers.add(new KeyValuePair("Accept", "application/json"));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("post",postText);
            jsonObject.put("facebook",crossPostToFacebook);
            jsonObject.put("twitter", crossPostToTwitter);
            JSONArray jsaFiles = new JSONArray();
            jsaFiles.put(base64encodedImage);
            jsonObject.put("files",jsaFiles);

            ResponseObject responseObject = HttpUtil.makeHttpRequestWithJsonParameters("v1/feed", "POST", headers, jsonObject);
            boolean success = responseObject.jsonResponse.optBoolean("success");

            return success;
        }
        catch(Exception ex)
        {
            return false;
        }
    }

    public static boolean addComment(String commentText, String feedItemId)
    {
        try
        {
            ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
            headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
            ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
            parameters.add(new KeyValuePair("post", commentText));

            ResponseObject responseObject = HttpUtil.makeHttpRequest("v1/feed/" + feedItemId + "/comments", "POST", headers, parameters);
            boolean success = responseObject.jsonResponse.optBoolean("success");

            return success;
        }
        catch(Exception ex)
        {
            return false;
        }
    }

    public static User getThisUser()
    {
        try
        {
            ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
            headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
            ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>(); //No parameters. Send an empty list.

            ResponseObject responseObject = HttpUtil.makeHttpRequestWithAppendedParameters("v1/users/default", "GET", headers, parameters);
            User user = JsonParser.parseUserFromResultsObject(responseObject.jsonResponse);
            return user;
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static void likeFeedItem(boolean liked, String feedItemId)
    {
        ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
        ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        if(liked)
            parameters.add(new KeyValuePair("like", "True"));
        else
            parameters.add(new KeyValuePair("like", "False"));

        ResponseObject responseObject = HttpUtil.makeHttpRequest("v1/feed/" + feedItemId + "/likes", "POST", headers, parameters);
    }

    public static void likeComment(boolean liked, String feedItemId, String commentId)
    {
        ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
        ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        if(liked)
            parameters.add(new KeyValuePair("like", "True"));
        else
            parameters.add(new KeyValuePair("like", "False"));

        ResponseObject responseObject = HttpUtil.makeHttpRequest("v1/feed/" + feedItemId + "/comments/" + commentId + "/likes", "POST", headers, parameters);
    }

    public static ArrayList<Comment> getComments(String feedItemId, int from, int limit)
    {
        ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
        headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
        ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
        parameters.add(new KeyValuePair("_limit", Integer.toString(limit)));
        parameters.add(new KeyValuePair("_from", Integer.toString(from)));

        ResponseObject responseObject = HttpUtil.makeHttpRequestWithAppendedParameters("v1/feed/" + feedItemId + "/comments", "GET", headers, parameters);
        JSONArray jsonCommentArray = responseObject.jsonResponse.optJSONArray("results");

        ArrayList<Comment> comments = JsonParser.parseCommentJsonArray(jsonCommentArray);
        return comments;
    }

    public static ArrayList<StreamItem> getStreamItems(int limit, long cursor, boolean discoverStreams)
    {
        try
        {
            ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
            headers.add(new KeyValuePair("Authorization", "Bearer " + AppState.getInstance().accessToken));
            ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
            if(discoverStreams) { parameters.add(new KeyValuePair("all", "true")); }

            ResponseObject responseObject = HttpUtil.makeHttpRequestWithAppendedParameters("v1/streaming", "GET", headers, parameters);
            ArrayList<StreamItem> streamItems = JsonParser.parseStreamItemResultsJson(responseObject.jsonResponse);
            return streamItems;
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public static ArrayList<FeedItem> getFeedItems(int limit, long cursor, boolean includePlayerMe, boolean includeYoutube, boolean includeTwitch, boolean showingDiscoverFeed)
    {
        try
        {
            AppState appState = AppState.getInstance();

            ArrayList<KeyValuePair> headers = new ArrayList<KeyValuePair>();
            headers.add(new KeyValuePair("Authorization", "Bearer " + appState.accessToken));
            ArrayList<KeyValuePair> parameters = new ArrayList<KeyValuePair>();
            parameters.add(new KeyValuePair("_limit", Integer.toString(limit)));
            if(cursor > 0)
                parameters.add(new KeyValuePair("_cursor", Long.toString(cursor)));
            if(showingDiscoverFeed)
                parameters.add(new KeyValuePair("general", "true"));
            String sourcesString = "";
            if (includePlayerMe) sourcesString += "playerme";
            if (includeYoutube) { if (sourcesString.length() > 0) { sourcesString += ",youtube"; } else { sourcesString += "youtube"; } }
            if (includeTwitch) { if (sourcesString.length() > 0) { sourcesString += ",twitch"; } else { sourcesString += "twitch"; } }
            parameters.add(new KeyValuePair("sources", sourcesString));

            ResponseObject responseObject = HttpUtil.makeHttpRequestWithAppendedParameters("v1/feed", "GET", headers, parameters);
            ArrayList<FeedItem> feedItems = JsonParser.parseFeedItemResultsJson(responseObject.jsonResponse);
            return feedItems;
        }
        catch(Exception ex)
        {
            Log.d("loadFeedItems","EXCEPTION when trying to get feed.");
            ex.printStackTrace();
            return new ArrayList<FeedItem>();
        }
    }

}

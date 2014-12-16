package me.player.player.Api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.player.player.AppState;
import me.player.player.Constants.Enums;
import me.player.player.Constants.Enums.FeedType;
import me.player.player.Constants.Enums.LoginError;
import me.player.player.Entities.Comment;
import me.player.player.Entities.CommentData;
import me.player.player.Entities.FeedItem;
import me.player.player.Entities.FeedItemData;
import me.player.player.Entities.FeedItemDataMeta;
import me.player.player.Entities.StreamItem;
import me.player.player.Entities.StreamItemData;
import me.player.player.Entities.User;
import me.player.player.Util;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class JsonParser {
    public static ArrayList<FeedItem> parseFeedItemResultsJson(JSONObject jsonFeedItemResults)
    {
        ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();
        JSONArray jsonFeedItemArray = jsonFeedItemResults.optJSONArray("results");
        boolean success = jsonFeedItemResults.optBoolean("success",false);
        if(!success)
            AppState.getInstance().loginError = LoginError.BAD_CREDENTIALS;
        for (int i = 0; i < jsonFeedItemArray.length(); i++)
        {
            JSONObject jsonFeedItem = jsonFeedItemArray.optJSONObject(i);
            FeedItem feedItem = parseFeedItemJson(jsonFeedItem);
            feedItemList.add(feedItem);
        }
        return feedItemList;
    }

    public static User parseUserFromResultsObject(JSONObject jsonUserResults)
    {
        JSONObject jsonUser = jsonUserResults.optJSONObject("results");
        User user = parseUserJson(jsonUser,false);
        return user;
    }

    public static ArrayList<StreamItem> parseStreamItemResultsJson(JSONObject jsonStreamItemResults)
    {
        ArrayList<StreamItem> streamItemList = new ArrayList<StreamItem>();
        JSONArray jsonStreamItemArray = jsonStreamItemResults.optJSONArray("results");
        boolean success = jsonStreamItemResults.optBoolean("success",false);
        if(!success)
            AppState.getInstance().loginError = LoginError.BAD_CREDENTIALS;
        for (int i = 0; i < jsonStreamItemArray.length(); i++)
        {
            JSONObject jsonStreamItem = jsonStreamItemArray.optJSONObject(i);
            StreamItem streamItem = parseStreamItemJson(jsonStreamItem);
            streamItemList.add(streamItem);
        }
        return streamItemList;
    }

    public static StreamItem parseStreamItemJson(JSONObject jsonStreamItem)
    {
        StreamItem streamItem = new StreamItem();
        SimpleDateFormat formatter = Util.getDateFormatter();

        streamItem.id = jsonStreamItem.optLong("id");
        streamItem.user_id = jsonStreamItem.optLong("user_id");
        streamItem.profile_id = jsonStreamItem.optString("profile_id");
        streamItem.provider = jsonStreamItem.optString("provider");
        streamItem.channel = jsonStreamItem.optString("channel");
        streamItem.url = jsonStreamItem.optString("url");
        streamItem.stream_id = jsonStreamItem.optString("stream_id");
        streamItem.status = jsonStreamItem.optString("status");
        streamItem.game_id = jsonStreamItem.optLong("id");
        streamItem.game_name = jsonStreamItem.optString("game_name");
        streamItem.thumbnail = jsonStreamItem.optString("thumbnail");
        streamItem.is_streaming = jsonStreamItem.optBoolean("is_streaming");
        JSONObject streamData = jsonStreamItem.optJSONObject("data");
        streamItem.data = parseStreamItemDataJson(streamData);
        String createdDateString = jsonStreamItem.optString("created_at");
        String updatedDateString = jsonStreamItem.optString("updated_at");
        Date createdDate, updatedDate;
        try
        {
            createdDate = formatter.parse(createdDateString);
            streamItem.created_at = createdDate;
            updatedDate = formatter.parse(updatedDateString);
            streamItem.updated_at = updatedDate;
        } catch (Exception ex) { }
        JSONObject jsonUser = jsonStreamItem.optJSONObject("user");
        streamItem.user = parseUserJson(jsonUser,false);

        return streamItem;
    }

    public static StreamItemData parseStreamItemDataJson(JSONObject jsonStreamItemData)
    {
        StreamItemData streamItemData = new StreamItemData();
        streamItemData.viewers = jsonStreamItemData.optLong("viewers");
        streamItemData.views = jsonStreamItemData.optLong("views");
        return streamItemData;
    }

    public static FeedItem parseFeedItemJson(JSONObject jsonfeedItem)
    {
        FeedItem feedItem = new FeedItem();
        SimpleDateFormat formatter = Util.getDateFormatter();
        //formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'SSSS");

        feedItem.id = jsonfeedItem.optLong("id");
        feedItem.userId = jsonfeedItem.optLong("user_id");
        feedItem.resourceId = jsonfeedItem.optLong("resource_id");
        String feedType = jsonfeedItem.optString("type");
        if (feedType.equalsIgnoreCase("Post"))
            feedItem.type = FeedType.POST;
        else if (feedType.equalsIgnoreCase("Video"))
            feedItem.type = FeedType.VIDEO;
        String feedSource = jsonfeedItem.optString("source");
        if (feedSource.equalsIgnoreCase("youtube"))
            feedItem.source = Enums.FeedFilter.YOUTUBE;
        else if (feedSource.equalsIgnoreCase("twitch"))
            feedItem.source = Enums.FeedFilter.TWITCH;
        else if (feedSource.equalsIgnoreCase("playerme"))
            feedItem.source = Enums.FeedFilter.PLAYERME;
        String publishedDateString = jsonfeedItem.optString("published_at");
        String createdDateString = jsonfeedItem.optString("created_at");
        String updatedDateString = jsonfeedItem.optString("updated_at");
        Date publishedDate, createdDate, updatedDate;
        try
        {
            publishedDate = formatter.parse(publishedDateString);
            feedItem.publishedDate = publishedDate;
            createdDate = formatter.parse(createdDateString);
            feedItem.createdDate = createdDate;
            updatedDate = formatter.parse(updatedDateString);
            feedItem.updatedDate = updatedDate;
        } catch (Exception ex) { }
        JSONObject jsonFeedItemData = jsonfeedItem.optJSONObject("data");
        feedItem.data = parseFeedItemDataJson(jsonFeedItemData);
        JSONObject jsonUser = jsonfeedItem.optJSONObject("user");
        feedItem.user = parseUserJson(jsonUser,true);
        feedItem.commentCount = jsonfeedItem.optInt("commentsCount");
        JSONArray jsonComments = jsonfeedItem.optJSONArray("comments");
        feedItem.comments = parseCommentJsonArray(jsonComments);

        feedItem.timeAgo = jsonfeedItem.optString("timeago");
        feedItem.hasLiked = jsonfeedItem.optBoolean("hasLiked");
        feedItem.sourceUrl = jsonfeedItem.optString("sourceUrl");
        feedItem.isSubscribed = jsonfeedItem.optBoolean("isSubscribed");
        feedItem.showDelete = jsonfeedItem.optBoolean("showDelete");
        feedItem.userIsHidden = jsonfeedItem.optBoolean("userIsHidden");
        feedItem.userIsBlocked = jsonfeedItem.optBoolean("userIsBlocked");
        feedItem.likesCount = jsonfeedItem.optInt("likesCount");

        return feedItem;
    }

    public static FeedItemData parseFeedItemDataJson(JSONObject jsonFeedItemData)
    {
        FeedItemData feedItemData = new FeedItemData();

        feedItemData.title = jsonFeedItemData.optString("title");
        feedItemData.description = jsonFeedItemData.optString("description");
        feedItemData.url = jsonFeedItemData.optString("url");
        feedItemData.thumbnailUrl = jsonFeedItemData.optString("thumbnail");
        feedItemData.post = jsonFeedItemData.optString("post");
        feedItemData.postRaw = jsonFeedItemData.optString("post_raw");
        JSONArray jsaMetas = jsonFeedItemData.optJSONArray("metas");
        feedItemData.metas =  new ArrayList<FeedItemDataMeta>();
        if(jsaMetas != null)
        {
            for (int i = 0; i < jsaMetas.length(); i++)
            {
                JSONObject jsonFeedItemDataMeta = jsaMetas.optJSONObject(i);
                FeedItemDataMeta feedItemDataMeta = parseFeedItemDataMeta(jsonFeedItemDataMeta);
                if(feedItemDataMeta.thumbnail.length() > 0)
                    feedItemData.hasPostedImageThumbnail = true;
                feedItemData.metas.add(feedItemDataMeta);
            }
        }

        return feedItemData;
    }

    public static FeedItemDataMeta parseFeedItemDataMeta(JSONObject jsonFeedItemDataMeta)
    {
        FeedItemDataMeta feedItemDataMeta = new FeedItemDataMeta();
        feedItemDataMeta.url = jsonFeedItemDataMeta.optString("url");
        feedItemDataMeta.title = jsonFeedItemDataMeta.optString("title");
        feedItemDataMeta.description = jsonFeedItemDataMeta.optString("description");
        feedItemDataMeta.provider = jsonFeedItemDataMeta.optString("provider");
        feedItemDataMeta.content = jsonFeedItemDataMeta.optString("content");
        feedItemDataMeta.isInternal = jsonFeedItemDataMeta.optBoolean("isInternal");
        feedItemDataMeta.thumbnail = jsonFeedItemDataMeta.optString("thumbnail");
        JSONArray jsaImages = jsonFeedItemDataMeta.optJSONArray("images");
        feedItemDataMeta.images = new ArrayList<String>();
        if(jsaImages != null)
        {
            for (int i = 0; i < jsaImages.length(); i++)
            {
                String imageString = jsaImages.optString(i);
                feedItemDataMeta.images.add(imageString);
            }
        }
        return feedItemDataMeta;
    }

    public static User parseUserJson(JSONObject jsonUser, boolean avatarIsString) //avatarIsString means the avatar url is just a string and not a separate json object.
    {
        User user = new User();
        SimpleDateFormat formatter = Util.getDateFormatter();

        user.id = jsonUser.optLong("id");
        user.username = jsonUser.optString("username");
        user.accountType = jsonUser.optString("account_type");
        user.slug = jsonUser.optString("slug");
        if(avatarIsString)
            user.avatarUrl = jsonUser.optString("avatar","");
        else
        {
            JSONObject jsonAvatar = jsonUser.optJSONObject("avatar"); //Sometimes avatar is buried in another json object
            if(jsonAvatar != null) user.avatarUrl = jsonAvatar.optString("cached","");
        }
        user.url = jsonUser.optString("url");
        user.isFeatured = jsonUser.optBoolean("is_featured");
        user.isVerified = jsonUser.optBoolean("is_verified");
        String createdDateString = jsonUser.optString("created_at","");
        if(createdDateString.length()>0)
        {
            try { user.createdAt = formatter.parse(createdDateString); } catch (Exception ex) { }
        }
        JSONObject jsonCover = jsonUser.optJSONObject("cover");
        if(jsonCover != null) user.coverUrl = jsonCover.optString("cached","");
        user.followersCount = jsonUser.optLong("followers_count",0);
        user.followingCount = jsonUser.optLong("following_count",0);

        return user;
    }

    public static ArrayList<Comment> parseCommentJsonArray(JSONArray jsonCommentArray)
    {
        ArrayList<Comment> comments =  new ArrayList<Comment>();
        for (int i = 0; i < jsonCommentArray.length(); i++)
        {
            JSONObject jsonComment = jsonCommentArray.optJSONObject(i);
            Comment comment = parseCommentJson(jsonComment);
            comments.add(comment);
        }
        return comments;
    }
    public static CommentData parseCommentDataJson(JSONObject jsonCommentData)
    {
        CommentData commentData = new CommentData();
        commentData.post = jsonCommentData.optString("post");
        commentData.postRaw = jsonCommentData.optString("post_raw");
        JSONArray jsaMetas = jsonCommentData.optJSONArray("metas");
        commentData.metas =  new ArrayList<FeedItemDataMeta>();
        if(jsaMetas != null)
        {
            for (int i = 0; i < jsaMetas.length(); i++)
            {
                JSONObject jsonFeedItemDataMeta = jsaMetas.optJSONObject(i);
                FeedItemDataMeta feedItemDataMeta = parseFeedItemDataMeta(jsonFeedItemDataMeta);
                if(feedItemDataMeta.thumbnail.length() > 0)
                    commentData.hasPostedImageThumbnail = true;
                commentData.metas.add(feedItemDataMeta);
            }
        }
        return commentData;
    }
    public static Comment parseCommentJson(JSONObject jsonComment)
    {
        Comment comment = new Comment();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        comment.id = jsonComment.optLong("id");
        comment.user_id = jsonComment.optLong("user_id");
        comment.activity_id = jsonComment.optLong("activity_id");

        JSONObject jsonCommentData = jsonComment.optJSONObject("data");
        if(jsonCommentData != null)
            comment.data = parseCommentDataJson(jsonCommentData);

        String createdDateString = jsonComment.optString("created_at");
        String updatedDateString = jsonComment.optString("updated_at");
        Date createdDate, updatedDate;
        try
        {
            createdDate = formatter.parse(createdDateString);
            comment.createdDate = createdDate;
            updatedDate = formatter.parse(updatedDateString);
            comment.updatedDate = updatedDate;
        }
        catch (Exception ex) { }
        comment.hasLiked = jsonComment.optBoolean("hasLiked");
        comment.timeago = jsonComment.optString("timeago");
        comment.url = jsonComment.optString("url");

        JSONObject jsonUser = jsonComment.optJSONObject("user");
        if(jsonUser != null)
            comment.user = parseUserJson(jsonUser,true);

        comment.userIsBlocked = jsonComment.optBoolean("userIsBlocked");
        comment.showDelete = jsonComment.optBoolean("showDelete");
        comment.showEdit = jsonComment.optBoolean("showEdit");
        comment.isOwnComment = jsonComment.optBoolean("isOwnComment");
        comment.likesCount = jsonComment.optInt("likesCount");

        return comment;
    }
}

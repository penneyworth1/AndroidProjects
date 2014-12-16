package me.player.player.Entities;

import java.util.ArrayList;
import java.util.Date;

import me.player.player.Constants.Enums;
import me.player.player.Constants.Enums.FeedType;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class FeedItem
{
    public long id;
    public long userId;
    public long resourceId;
    public Enums.FeedFilter source;
    public FeedType type;
    public FeedItemData data;
    public Date publishedDate;
    public Date createdDate;
    public Date updatedDate;
    public User user;
    public int commentCount;
    public ArrayList<Comment> comments;
    public String timeAgo;
    public boolean hasLiked;
    public String sourceUrl;
    public boolean isSubscribed;
    public boolean showDelete;
    public boolean userIsHidden;
    public boolean userIsBlocked;
    public int likesCount;

    public FeedItem()
    {

    }



}

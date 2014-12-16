package me.player.player.Entities;

import java.util.Date;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class Comment
{
    public long id;
    public long user_id;
    public long activity_id;
    public CommentData data;
    public Date createdDate;
    public Date updatedDate;
    public boolean hasLiked;
    public String timeago;
    public String url;
    public User user;
    public boolean userIsBlocked;
    public boolean showDelete;
    public boolean showEdit;
    public boolean isOwnComment;
    public int likesCount;

    public Comment()
    {

    }
}

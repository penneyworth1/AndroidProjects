package me.player.player.Entities;

import java.util.Date;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class User
{
    public long id;
    public String username;
    public String accountType;
    public String slug;
    public String avatarUrl;
    public String coverUrl;
    public String url;
    public boolean isFeatured;
    public boolean isVerified;
    public Date createdAt;
    public long followersCount;
    public long followingCount;

    public User()
    {

    }
}

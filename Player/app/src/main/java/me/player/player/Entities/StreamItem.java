package me.player.player.Entities;

import java.util.Date;

/**
 * Created by stevenstewart on 9/20/14.
 */
public class StreamItem
{
    public long id;
    public long user_id;
    public String profile_id;
    public String provider;
    public String channel;
    public String url;
    public String stream_id;
    public String status;
    public long game_id;
    public String game_name;
    public String thumbnail;
    public boolean is_streaming;
    public StreamItemData data;
    public String viewersString;
    public Date created_at;
    public Date updated_at;
    public User user;
    public ImageFromServer thumbnailImageFromServer;
    public ImageFromServer avatarImageFromServer;

    public StreamItem()
    {

    }
}

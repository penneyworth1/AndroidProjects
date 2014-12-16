package me.player.player.Entities;


import java.util.ArrayList;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class FeedItemData
{
    public String title;
    public String description;
    public String url;
    public String thumbnailUrl;
    public String post;
    public String postRaw;
    public ImageFromServer thumbnailImageFromServer;
    public ImageFromServer avatarImageFromServer;
    public ArrayList<FeedItemDataMeta> metas;
    public boolean hasPostedImageThumbnail = false;

    public FeedItemData()
    {

    }
}

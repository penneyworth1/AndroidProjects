package me.player.player.Entities;

import java.util.ArrayList;

/**
 * Created by stevenstewart on 9/7/14.
 */
public class CommentData
{
    public String post;
    public String postRaw;
    public ImageFromServer avatarImageFromServer;
    public ArrayList<FeedItemDataMeta> metas;
    public boolean hasPostedImageThumbnail = false;

    public CommentData()
    {

    }
}

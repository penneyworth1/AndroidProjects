package me.player.player.Entities;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.ArrayList;

import me.player.player.Constants.Enums.StretchMode;

/**
 * Created by stevenstewart on 9/12/14.
 */
public class ImageFromServer
{
    public ImageFromServer()
    {

    }

    public String url = "";
    public boolean downloaded = false;
    public boolean downloadFailed = false;
    public boolean skipNextDownloadAttempt = false;
    public int downloadFailCount = 0;
    public int mostRecentAdapterIndex = 0;
    public Drawable drawable = null;
    public int importance = 1; //1 is least likely to be recycled.
    public ArrayList<ImageView> ivsToBePopulated = new ArrayList<ImageView>();
    public boolean isAvatar = false;
}

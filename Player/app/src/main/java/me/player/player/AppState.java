package me.player.player;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import me.player.player.Constants.Enums.*;
import me.player.player.Constants.Names;
import me.player.player.Entities.FeedItem;
import me.player.player.Entities.StreamItem;
import me.player.player.Entities.User;

/**
 * Created by stevenstewart on 8/16/14.
 */

public class AppState
{
    private static AppState appState;
    private AppState()
    {
        //Init global variables
        normalFont = Typeface.create("sans-serif-light", Typeface.NORMAL);
        boldFont = Typeface.create("sans-serif", Typeface.BOLD);
        backButtonCommands = new ArrayList<BackButtonCommand>();
    }
    public static AppState getInstance()
    {
        if (appState == null)
        {
            appState = new AppState();
        }
        return appState;
    }
    public void initSharedPreferences()
    {
        SharedPreferences prefs = Util.getSharedPreferences(appState.mainActivity);
        appState.accessToken = prefs.getString(Names.PREFS_KEY_ACCESS_TOKEN,"");
        appState.refreshToken = prefs.getString(Names.PREFS_KEY_REFRESH_TOKEN,"");
        appState.enteredUsername = prefs.getString(Names.PREFS_KEY_USERNAME,"");
    }


    public int screenWidth;
    public int screenHeight;
    public MainActivity mainActivity;
    public RelativeLayout baseContentLayout;
    public boolean uiEnabled;
    public Typeface normalFont;
    public Typeface boldFont;

    //UI input that we need to keep track of
    public String enteredUsername = "";
    public String enteredPassword = "";

    //Current user
    public User thisUser;
    public String currentUserAvatarResize;
    public Drawable currentUserCircularAvatar;

    //Network Api related
    public String accessToken;
    public String refreshToken;
    public LoginError loginError = LoginError.NONE;
    public boolean tooManyItemsqueuedForDownload = false;
    public long millisBeforeNoDataFail = 2000; //how long to wait to receive the first byte before giving up
    public long millisBeforeIncompleteDataFail = 5000; //how long to wait to receive all data before giving up
    public String base64encodedImageString = ""; //Here we store the image about to be posted.

    //Back button related
    public ArrayList<BackButtonCommand> backButtonCommands;

    //Menu related
    public boolean menuOpen = false;


    //Derived measurements
    //-----------------------------------------------------------------------------------------------
    public void initDerivedMeasurements()
    {
        loginTitleHeight = Util.pixelNumberForDp(100,this);
        feedSourcesTopMargin = Util.pixelNumberForDp(121,this);
        feedSourcesUnderlineTopMargin = Util.pixelNumberForDp(151,this);
        feedSourcesUnderlineHeight = Util.pixelNumberForDp(6,this);
        feedGridTopBlankItemHeight = Util.pixelNumberForDp(157,this);
        feedFilterIconHeight = Util.pixelNumberForDp(44,this);
        feedItemMarginTop = Util.pixelNumberForDp(10,this);
        feedItemMarginRight = Util.pixelNumberForDp(5,this);
        feedItemHeight = Util.pixelNumberForDp(219,this);
        feedItemHeightWithoutThumbnail = Util.pixelNumberForDp(160,this);
        feedItemTypeIndicatorHeight = Util.pixelNumberForDp(35,this);
        feedItemBottomBarHeight = Util.pixelNumberForDp(45,this);
        feedItemRightColorBarWidth = Util.pixelNumberForDp(8, this);
        feedItemIconHeight = Util.pixelNumberForDp(24, this);
        feedItemUsernameLeftMargin = Util.pixelNumberForDp(63);
        feedItemUsernameTopMargin = Util.pixelNumberForDp(19);
        feedItemThumbnailWidth = Util.pixelNumberForDp(150, this);
        feedItemThumbnailHeight = Util.pixelNumberForDp(90, this);
        feedItemThumbnailResize = "-" + Integer.toString(feedItemThumbnailWidth) + "x" + Integer.toString(feedItemThumbnailHeight);
        feedItemThumbnailLeftMargin = Util.pixelNumberForDp(10, this);
        feedItemThumbnailTopMargin = Util.pixelNumberForDp(65, this);
        feedItemAvatarResize = "-" + Integer.toString(Util.pixelNumberForDp(50, this)) + "x" + Integer.toString(Util.pixelNumberForDp(50, this));
        streamItemAvatarResize = "-" + Integer.toString(Util.pixelNumberForDp(32, this)) + "x" + Integer.toString(Util.pixelNumberForDp(32, this));
        currentUserAvatarResize = "-" + Integer.toString(Util.pixelNumberForDp(60, this)) + "x" + Integer.toString(Util.pixelNumberForDp(60, this));
        distanceToOverScrollBeforeRefresh = Util.pixelNumberForDp(150);
        createNewPostIconHeight = Util.pixelNumberForDp(27);
        createNewPostPictureIconHeight = Util.pixelNumberForDp(30);
    }
    //Login page
    public int loginTitleHeight;

    //Feed page
    public String feedItemThumbnailResize;
    public String feedItemAvatarResize;
    public String streamItemAvatarResize;
    public int feedSourcesTopMargin;
    public int feedSourcesUnderlineTopMargin;
    public int feedSourcesUnderlineHeight;
    public int feedFilterIconHeight;
    public int feedItemMarginTop;
    public int feedItemMarginRight;
    public int feedItemHeight;
    public int feedItemHeightWithoutThumbnail;
    public int feedItemTypeIndicatorHeight;
    public int feedItemBottomBarHeight;
    public int feedItemRightColorBarWidth;
    public int feedItemIconHeight;
    public int feedItemThumbnailWidth;
    public int feedItemThumbnailHeight;
    public int feedItemThumbnailLeftMargin;
    public int feedItemThumbnailTopMargin;
    public int feedItemUsernameLeftMargin;
    public int feedItemUsernameTopMargin;
    public int feedGridTopBlankItemHeight;

    //Misc
    public int distanceToOverScrollBeforeRefresh;
    public int createNewPostIconHeight;
    public int createNewPostPictureIconHeight;

    //-----------------------------------------------------------------------------------------------
    //End derived measurements


    //Drawables that are created from images from assets. They will be set when a page is loaded, and released when a new page is loaded.
    //We will keep track of these in AppState because a page may use these values in classes other than the page manager, for example, an image adapter.
    //-----------------------------------------------------------------------------------------------
    public Drawable drwblLoginTitle = null;
    public Drawable drwblLoginUserIcon = null;
    public Drawable drwblLoginLockIcon = null;
    public Drawable drwblMenuButton = null;
    public Drawable drwblCreatePost = null;
    public Drawable drwblCreatePostCancel = null;
    public Drawable drwblPlayerFilterIcon = null;
    public Drawable drwblPlayerFilterIconInverted = null;
    public Drawable drwblPlayerFeedItemIndicator = null;
    public Drawable drwblYoutubeFilterIcon = null;
    public Drawable drwblYoutubeFilterIconInverted = null;
    public Drawable drwblYoutubeFeedItemIndicator = null;
    public Drawable drwblTwitchFilterIcon = null;
    public Drawable drwblTwitchFilterIconInverted = null;
    public Drawable drwblTwitchFeedItemIndicator = null;
    public Drawable drwblFacebookFilterIcon = null;
    public Drawable drwblTwitterFilterIcon = null;
    public Drawable drwblCommentIcon = null;
    public Drawable drwblEyeIcon = null;
    public Drawable drwblLikeIcon = null;
    public Drawable drwblLikedIcon = null;
    public Drawable drwblTimeIcon = null;
    public Drawable drwblPictureIcon = null;



    //-----------------------------------------------------------------------------------------------
    //End drawables from assets


    //Collections that are used by multiple classes
    //Feed items that have just been retrieved from the server. These will be added to the list in the adapter.
    public ArrayList<FeedItem> fetchedFeedItems = new ArrayList<FeedItem>(); //All the feed items we already have.
    public ArrayList<StreamItem> fetchedStreamItems = new ArrayList<StreamItem>();
    public FeedItem selectedFeedItem;
    public int numberOfCommentsShowing;

    //Range of current gridview positions
    public int adapterIndexRangeStart = 0;
    public int adapterIndexRangeEnd = 20;
    public void reportValidIndexRange(int start, int end) //any items outside this range have scrolled off screen and should be cancelled if they are not already downloaded.
    {
        if(start == 0 & end == 0) //This somehow gets called with 0,0. Override this scenario.
        {
            adapterIndexRangeStart = -1;
            adapterIndexRangeEnd = 50;
        }
        else
        {
            adapterIndexRangeStart = start;
            adapterIndexRangeEnd = end;
        }
    }
}

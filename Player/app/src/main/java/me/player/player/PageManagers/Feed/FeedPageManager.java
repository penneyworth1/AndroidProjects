package me.player.player.PageManagers.Feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import me.player.player.AnimationManager;
import me.player.player.Api.ApiMethods;
import me.player.player.Api.DownloadManager;
import me.player.player.Constants.Enums;
import me.player.player.Constants.Enums.AnimationType;
import me.player.player.Constants.Enums.AppError;
import me.player.player.Constants.Enums.LoginError;
import me.player.player.Constants.TimeMeasurements;
import me.player.player.Constants.Numbers;
import me.player.player.Entities.FeedItem;
import me.player.player.Entities.FeedItemData;
import me.player.player.Entities.FeedItemDataMeta;
import me.player.player.Entities.ImageFromServer;
import me.player.player.Entities.StreamItem;
import me.player.player.ExtendedAndroidObjects.GridViewWithOverScroll;
import me.player.player.PageManagers.BasePageManager;
import me.player.player.PageManagers.Feed.Listeners.CreatePostButtonTouchListener;
import me.player.player.PageManagers.Feed.Listeners.DiscoverButtonTouchListener;
import me.player.player.PageManagers.Feed.Listeners.FollowingButtonTouchListener;
import me.player.player.PageManagers.Feed.Listeners.SearchButtonTouchListener;
import me.player.player.PageManagers.Feed.Listeners.StreamingButtonTouchListener;
import me.player.player.PageManagers.Feed.Listeners.TopBarTouchListener;
import me.player.player.R;
import me.player.player.Util;
import me.player.player.ViewUtil;

/**
 * Created by stevenstewart on 8/21/14.
 */

public class FeedPageManager extends BasePageManager
{

    //UI elements
    RelativeLayout rlGradient;
    RelativeLayout rlTitleBar;
    View vTitleBarUnderline;
    View vSourcesBackground;
    View vFeedTypeBackground;
    View vPullDownPercentageBar;
    TextView tvTitle;
    ImageView ivCreatePost;
    TextView tvFollowingButton;
    int followingButtonTextWidth;
    int followingButtonLeftMargin;
    TextView tvDiscoverButton;
    int discoverButtonTextWidth;
    int discoverButtonLeftMargin;
    TextView tvStreamingButton;
    int streamingButtonTextWidth;
    int streamingButtonLeftMargin;
    TextView tvSearchButton;
    int searchButtonTextWidth;
    int searchButtonLeftMargin;
    View vFeedSourceUnderline;
    float screenWidthOver6;
    float screenWidthOver8;
    int gridViewVisibleItemStartIndex = 0;
    int gridViewVisibleItemEndIndex = 99;

    //Create new post UI elements
    RelativeLayout rlCreateNewPost;
    ImageView ivCancelNewPost;
    TextView tvCharactersRemainingForNewPost;
    ImageView ivCreateNewPostAvatar;
    TextView tvCreateNewPostUsername;
    RelativeLayout rlPostButtonFrame;
    ImageView ivPostButtonWireFrame;
    TextView tvPostButtonText;
    EditText etNewPostText;
    View vCreateNewPostBottomBar;
    ImageView ivCreateNewPostSelectPicture;
    ImageView ivCreateNewPostFacebook;
    ImageView ivCreateNewPostTwitter;
    boolean crossPostingToFacebook = false;
    boolean crossPostingToTwitter = false;

    //Post detail with comments UI elements
    RelativeLayout rlPostDetail;
    View vPostDetailTopBar;
    ImageView ivClosePostDetail;
    TextView tvPostDetailTitle;
    View vPostDetailTopBarUnderline;
    GridViewWithOverScroll gvPostDetail;
    PostDetailAdapter postDetailAdapter;
    View vPostDetailBottomBar;
    EditText etAddComment;
    TextView tvPostCommentButton;
    float animationOriginX = 0;
    float animationOriginY = 0;

    //filter icons
    ImageView ivPlayerFilter;
    ImageView ivPlayerFilterInverted;
    ImageView ivYoutubeFilter;
    ImageView ivYoutubeFilterInverted;
    ImageView ivTwitchFilter;
    ImageView ivTwitchFilterInverted;

    //Filters and source types.
    boolean showingYoutubeFeedItems = true;
    boolean showingPlayerFeedItems = true;
    boolean showingTwitchFeedItems = true;
    boolean showingDiscoverFeed = false;
    boolean showingStreaming = false;

    //The gridviews to hold all feed items.
    GridViewWithOverScroll gvFeed;
    FeedItemAdapter feedItemAdapter;
    GridViewWithOverScroll gvStreaming;
    StreamItemAdapter streamItemAdapter;
    long feedCursor;
    Object feedItemListLock = new Object();
    boolean downloadsCancelled = false;
    long itemFetchRequesId = 0; //This is used to make sure only the latest request is honored when multiple requests are made while a request is still in progress. The one in progress should finish, and then only the most recent one after that should start.
    boolean refreshingItemList = false;
    boolean loadingMoreItemsAtTheBottomOfTheList = false;
    int currentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    boolean overScrollingUp = false;
    float previousTouchY = 0;
    float totalOverscrollYLength = 0; //To keep track of how far down we have dragged since over-scrolling began


    public FeedPageManager(Context contextPar)
    {
        super(contextPar);
    }

    @Override
    public AppError init()
    {
        screenWidthOver6 = ((float)appState.screenWidth)/6;
        screenWidthOver8 = ((float)appState.screenWidth)/8;

        rlGradient = new RelativeLayout(context);
        ViewUtil.initRadialGradientRelativeLayout(rlGradient,new int[] {0xFF1B2133,0xFFDBB0A2},appState.screenHeight,1.01f,0.8f,false,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);

        initFeedGridView();
        initStreamGridView();

        rlTitleBar = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(rlTitleBar,Color.parseColor("#EE222222"),false,false,false,RelativeLayout.LayoutParams.MATCH_PARENT, Util.pixelNumberForDp(50),0,0,0,0);
        rlTitleBar.setOnTouchListener(new TopBarTouchListener());
        vTitleBarUnderline = new View(context);
        ViewUtil.initBox(vTitleBarUnderline,Color.parseColor("#00C0C8"),false,false,false,RelativeLayout.LayoutParams.MATCH_PARENT,Util.pixelNumberForDp(3),0,Util.pixelNumberForDp(50),0,0);
        vSourcesBackground = new View(context);
        ViewUtil.initBox(vSourcesBackground,Color.parseColor("#DDFFFFFF"),false,false,false,RelativeLayout.LayoutParams.MATCH_PARENT,Util.pixelNumberForDp(55),0,Util.pixelNumberForDp(53),0,0);
        vFeedTypeBackground = new View(context);
        ViewUtil.initBox(vFeedTypeBackground,Color.parseColor("#EDEDED"),false,false,false,RelativeLayout.LayoutParams.MATCH_PARENT,Util.pixelNumberForDp(50),0,Util.pixelNumberForDp(107),0,0);
        vPullDownPercentageBar = new View(context);
        ViewUtil.initBox(vPullDownPercentageBar,Color.GREEN,true,false,false,RelativeLayout.LayoutParams.MATCH_PARENT,Util.pixelNumberForDp(4),0,appState.feedGridTopBlankItemHeight+2,0,0);



        //Source type buttons. We need the widths to resize the underline and to arrange them in a grid.
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        tvFollowingButton = new TextView(context);
        ViewUtil.initTextView(tvFollowingButton,getString(R.string.feed_following),16,false,Color.BLACK,false,false,false,false,0,0,0,0);
        followingButtonTextWidth = Util.getTextViewWidth(tvFollowingButton);
        followingButtonLeftMargin = (int)(screenWidthOver8-followingButtonTextWidth/2);
        tvFollowingButton.setOnTouchListener(new FollowingButtonTouchListener());
        ((RelativeLayout.LayoutParams)tvFollowingButton.getLayoutParams()).setMargins(followingButtonLeftMargin,appState.feedSourcesTopMargin,0,0);
        tvDiscoverButton = new TextView(context);
        ViewUtil.initTextView(tvDiscoverButton,getString(R.string.feed_discover),16,false,Color.BLACK,false,false,false,false,0,0,0,0);
        discoverButtonTextWidth = Util.getTextViewWidth(tvDiscoverButton);
        discoverButtonLeftMargin = (int)(3*screenWidthOver8-discoverButtonTextWidth/2);
        tvDiscoverButton.setOnTouchListener(new DiscoverButtonTouchListener());
        ((RelativeLayout.LayoutParams)tvDiscoverButton.getLayoutParams()).setMargins(discoverButtonLeftMargin, appState.feedSourcesTopMargin, 0, 0);
        tvStreamingButton = new TextView(context);
        ViewUtil.initTextView(tvStreamingButton,getString(R.string.feed_streaming),16,false,Color.BLACK,false,false,false,false,0,0,0,0);
        streamingButtonTextWidth = Util.getTextViewWidth(tvStreamingButton);
        streamingButtonLeftMargin = (int)(5*screenWidthOver8-streamingButtonTextWidth/2);
        tvStreamingButton.setOnTouchListener(new StreamingButtonTouchListener());
        ((RelativeLayout.LayoutParams)tvStreamingButton.getLayoutParams()).setMargins(streamingButtonLeftMargin,appState.feedSourcesTopMargin,0,0);
        tvSearchButton = new TextView(context);
        ViewUtil.initTextView(tvSearchButton,getString(R.string.feed_streaming_discover),16,false,Color.BLACK,false,false,false,false,0,0,0,0);
        searchButtonTextWidth = Util.getTextViewWidth(tvSearchButton);
        searchButtonLeftMargin = (int)(7*screenWidthOver8-searchButtonTextWidth/2);
        tvSearchButton.setOnTouchListener(new SearchButtonTouchListener());
        ((RelativeLayout.LayoutParams)tvSearchButton.getLayoutParams()).setMargins(searchButtonLeftMargin,appState.feedSourcesTopMargin,0,0);
        vFeedSourceUnderline = new View(context);
        ViewUtil.initBox(vFeedSourceUnderline,Color.parseColor("#1FE0CD"),false,false,false,1, appState.feedSourcesUnderlineHeight,0,appState.feedSourcesUnderlineTopMargin,0,0);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Top bar UI
        tvTitle = new TextView(context);
        ViewUtil.initTextView(tvTitle, getString(R.string.feed_title), 25,false, Color.WHITE, true,false,false,false, 0, Util.pixelNumberForDp(8), 0, 0);
        ivCreatePost = new ImageView(context);
        ViewUtil.initImageView(ivCreatePost,ImageView.ScaleType.FIT_XY,false,true,false,0,Util.pixelNumberForDp(10),Util.pixelNumberForDp(10),0);
        ivCreatePost.setOnTouchListener(new CreatePostButtonTouchListener());

        initFilterButtons();

        //Modal Dialogs//
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //Create-new-post UI elements
        rlCreateNewPost = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(rlCreateNewPost,Color.parseColor("#FA272C38"),false,false,false,appState.screenWidth,Util.pixelNumberForDp(300),0,0,0,0);
        rlCreateNewPost.setVisibility(View.GONE);
        rlCreateNewPost.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true; //just make sure no touches go through this view to interact with views underneath it.
            }
        });
        ivCancelNewPost = new ImageView(context);
        ViewUtil.initImageView(rlCreateNewPost,ivCancelNewPost,ImageView.ScaleType.FIT_XY,false,false,false,Util.pixelNumberForDp(25),Util.pixelNumberForDp(20),0,0);
        ivCancelNewPost.setVisibility(View.VISIBLE);
        ivCancelNewPost.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    showCreateNewPostWindow(false);
                    appState.mainActivity.popBackButtonCommand(Enums.BackButtonCommand.DISMISS_CREATE_POST_DIALOG);
                }
                return true;
            }
        });
        ivCreateNewPostAvatar = new ImageView(context);
        ViewUtil.initImageView(rlCreateNewPost,ivCreateNewPostAvatar,ImageView.ScaleType.FIT_XY,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(70),0,0);
        ivCreateNewPostAvatar.setVisibility(View.VISIBLE);
        tvCreateNewPostUsername = new TextView(context);
        ViewUtil.initTextView(rlCreateNewPost, tvCreateNewPostUsername, "-", 17, true, Color.WHITE, false,false, false, false, Util.pixelNumberForDp(87), Util.pixelNumberForDp(84), 0, 0);
        rlPostButtonFrame = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(rlCreateNewPost,rlPostButtonFrame,Color.parseColor("#00000000"),false,true,false,Util.pixelNumberForDp(120),Util.pixelNumberForDp(70),0,0,0,0);
        ivPostButtonWireFrame = new ImageView(context);
        ViewUtil.initRoundRectWireframeImageView(rlPostButtonFrame,ivPostButtonWireFrame,false,false,false,Color.WHITE,Util.pixelNumberForDp(100),Util.pixelNumberForDp(50),Util.pixelNumberForDp(10),Util.pixelNumberForDp(10),0,0);
        tvPostButtonText = new TextView(context);
        ViewUtil.initTextView(rlPostButtonFrame, tvPostButtonText, getString(R.string.feed_post), 17, true, Color.WHITE, true, true, false, false, 0, 0, 0, 0);
        etNewPostText = new EditText(context);
        ViewUtil.initEditText(rlCreateNewPost,etNewPostText,18,getString(R.string.feed_post_hint),Color.WHITE,Color.parseColor("#00000000"),Color.parseColor("#88FFFFFF"),appState.screenWidth-Util.pixelNumberForDp(20),Util.pixelNumberForDp(100),true,false,false,false,0,Util.pixelNumberForDp(140),0,0,false);
        etNewPostText.setEnabled(false);
        etNewPostText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Numbers.NEW_POST_MAX_CHARACTERS)});
        etNewPostText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s)
            {
                int charsRemaining = Numbers.NEW_POST_MAX_CHARACTERS - s.length();
                tvCharactersRemainingForNewPost.setText(Integer.toString(charsRemaining));
            }
        });
        tvCharactersRemainingForNewPost = new TextView(context);
        ViewUtil.initTextView(rlCreateNewPost,tvCharactersRemainingForNewPost,"700",16,false,Color.parseColor("#FF999999"),false,false,true,false,0,Util.pixelNumberForDp(23),Util.pixelNumberForDp(120),0);
        vCreateNewPostBottomBar = new View(context);
        ViewUtil.initBox(rlCreateNewPost,vCreateNewPostBottomBar,Color.parseColor("#FF000000"),false,false,true,appState.screenWidth,Util.pixelNumberForDp(50),0,0,0,0);
        ivCreateNewPostSelectPicture = new ImageView(context);
        ViewUtil.initImageView(rlCreateNewPost,ivCreateNewPostSelectPicture,ImageView.ScaleType.FIT_XY,false,true,true,0,0,Util.pixelNumberForDp(10),Util.pixelNumberForDp(10));
        ivCreateNewPostFacebook = new ImageView(context);
        ViewUtil.initImageView(rlCreateNewPost,ivCreateNewPostFacebook,ImageView.ScaleType.FIT_XY,false,false,true,Util.pixelNumberForDp(10),0,0,Util.pixelNumberForDp(10));
        ivCreateNewPostFacebook.setAlpha(0.3f);
        ivCreateNewPostTwitter = new ImageView(context);
        ViewUtil.initImageView(rlCreateNewPost,ivCreateNewPostTwitter,ImageView.ScaleType.FIT_XY,false,false,true,Util.pixelNumberForDp(50),0,0,Util.pixelNumberForDp(10));
        ivCreateNewPostTwitter.setAlpha(0.3f);
        ivCreateNewPostSelectPicture.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    appState.mainActivity.startActivityForResult(photoPickerIntent, Numbers.ACTIVITY_RESULT_SELECT_PHOTO_FOR_CREATE_POST);
                }
                return true;
            }
        });
        ivCreateNewPostFacebook.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    if (crossPostingToFacebook)
                        AnimationManager.fadeOutPartially(ivCreateNewPostFacebook, .3f);
                    else
                        AnimationManager.fadeInFromAnyAlphaValue(ivCreateNewPostFacebook);
                    crossPostingToFacebook = !crossPostingToFacebook;
                }
                return true;
            }
        });
        ivCreateNewPostTwitter.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    if (crossPostingToTwitter)
                        AnimationManager.fadeOutPartially(ivCreateNewPostTwitter, .3f);
                    else
                        AnimationManager.fadeInFromAnyAlphaValue(ivCreateNewPostTwitter);
                    crossPostingToTwitter = !crossPostingToTwitter;
                }
                return true;
            }
        });
        ivPostButtonWireFrame.setOnTouchListener(new OnTouchListener() //Send the post to the server!
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    appState.mainActivity.showLoadingAnimation();
                    appState.uiEnabled = false;
                    AnimationManager.addAnimation(ivPostButtonWireFrame,AnimationType.DISMISS_FADE_OUT_SHRINK);
                    AnimationManager.addAnimation(tvPostButtonText,AnimationType.DISMISS_FADE_OUT_SHRINK);

                    //Run api method on seperate thread.
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            final boolean postSuccess;
                            if(appState.base64encodedImageString.length() > 0)
                                postSuccess = ApiMethods.createNewPostWithImage(etNewPostText.getText().toString(),crossPostingToFacebook,crossPostingToTwitter,appState.base64encodedImageString);
                            else
                                postSuccess = ApiMethods.createNewPost(etNewPostText.getText().toString(),crossPostingToFacebook,crossPostingToTwitter);

                            //Post result events on the UI thread.
                            new Handler(Looper.getMainLooper()).post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    appState.mainActivity.dismissLoadingAnimation();
                                    appState.uiEnabled = true;
                                    if(postSuccess)
                                    {
                                        etNewPostText.setText("");
                                        refreshFeedItems();
                                        showCreateNewPostWindow(false);
                                        appState.mainActivity.popBackButtonCommand(Enums.BackButtonCommand.DISMISS_CREATE_POST_DIALOG);
                                    }
                                    else
                                    {
                                        AnimationManager.addAnimation(ivPostButtonWireFrame,AnimationType.SHOW_FADE_IN);
                                        AnimationManager.addAnimation(tvPostButtonText,AnimationType.SHOW_FADE_IN_FROM_RIGHT);
                                    }

                                }
                            });
                        }
                    }).start();
                }
                return true;
            }
        });

        //Post detail and comments UI Elements
        rlPostDetail = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(rlPostDetail, Color.parseColor("#EE7B5D67"), false, false, false, appState.screenWidth, appState.screenHeight, 0, 0, 0, 0);
        rlPostDetail.setVisibility(View.GONE);
        rlPostDetail.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true; //just make sure no touches go through this view to interact with views underneath it.
            }
        });
        initPostDetailGridView();
        vPostDetailTopBar = new View(context);
        ViewUtil.initBox(rlPostDetail,vPostDetailTopBar,Color.parseColor("#DD272D39"),false,false,false,appState.screenWidth,Util.pixelNumberForDp(70),0,0,0,0);
        vPostDetailTopBarUnderline = new View(context);
        ViewUtil.initBox(rlPostDetail,vPostDetailTopBarUnderline,Color.parseColor("#02C1C9"),false,false,false,appState.screenWidth,Util.pixelNumberForDp(4),0,Util.pixelNumberForDp(70),0,0);
        ivClosePostDetail = new ImageView(context);
        ViewUtil.initImageView(rlPostDetail, ivClosePostDetail, ImageView.ScaleType.FIT_XY, false, false, false, Util.pixelNumberForDp(25), Util.pixelNumberForDp(20), 0, 0);
        ivClosePostDetail.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    appState.mainActivity.popBackButtonCommand(Enums.BackButtonCommand.DISMISS_POST_DETAIL_DIALOG);
                    showPostDetailWindow(false);
                }
                return true;
            }
        });
        tvPostDetailTitle = new TextView(context);
        ViewUtil.initTextView(rlPostDetail,tvPostDetailTitle,getString(R.string.feed_post_detail_title),22,true,Color.WHITE,true,false,false,false,0,Util.pixelNumberForDp(20),0,0);
        vPostDetailBottomBar = new View(context);
        ViewUtil.initBox(rlPostDetail,vPostDetailBottomBar,Color.parseColor("#EE2A303B"),false,false,true,appState.screenWidth,Util.pixelNumberForDp(60),0,0,0,0);
        etAddComment = new EditText(context);
        ViewUtil.initEditTextWithRoundCorners(rlPostDetail, etAddComment, 17, getString(R.string.feed_post_comment_hint), Color.WHITE, Color.BLACK, Color.parseColor("#555555"), appState.screenWidth - Util.pixelNumberForDp(100), Util.pixelNumberForDp(40), false, false, false, true, Util.pixelNumberForDp(10), 0, 0, Util.pixelNumberForDp(10), false, Util.pixelNumberForDp(10), true);
        tvPostCommentButton = new TextView(context);
        ViewUtil.initTextView(rlPostDetail,tvPostCommentButton,getString(R.string.feed_post_comment_button_text),22,false,Color.LTGRAY,false,false,true,true,0,0,Util.pixelNumberForDp(17),Util.pixelNumberForDp(17));
        tvPostCommentButton.setOnTouchListener(new OnTouchListener() //Send the post to the server!
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled && etAddComment.getText().length()>0)
                {
                    appState.mainActivity.showLoadingAnimation();
                    appState.uiEnabled = false;
                    AnimationManager.addAnimation(tvPostCommentButton,AnimationType.DISMISS_FADE_OUT_SHRINK);

                    //Run api method on seperate thread.
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            final boolean postSuccess = ApiMethods.addComment(etAddComment.getText().toString(),Long.toString(appState.selectedFeedItem.id));
                            if(postSuccess)
                            { //Reload comments
                                appState.numberOfCommentsShowing++;
                                appState.selectedFeedItem.comments = ApiMethods.getComments(Long.toString(appState.selectedFeedItem.id),0,appState.numberOfCommentsShowing);
                                appState.selectedFeedItem.commentCount++;
                            }

                            //Post result events on the UI thread.
                            new Handler(Looper.getMainLooper()).post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    appState.mainActivity.dismissLoadingAnimation();
                                    appState.uiEnabled = true;
                                    if(postSuccess)
                                    {
                                        etAddComment.setText("");
                                        postDetailAdapter.notifyDataSetChanged();
                                        appState.mainActivity.dismissSoftKeyboard();
                                        gvPostDetail.smoothScrollToPosition(postDetailAdapter.getCount()-1);
                                        //refreshFeedItems();
                                        //showCreateNewPostWindow(false);
                                        //appState.mainActivity.popBackButtonCommand(Enums.BackButtonCommand.DISMISS_CREATE_POST_DIALOG);
                                    }
                                    else
                                    {

                                    }
                                    AnimationManager.addAnimation(tvPostCommentButton,AnimationType.SHOW_FADE_IN);
                                }
                            });
                        }
                    }).start();
                }
                return true;
            }
        });

        return AppError.NONE;
    }
    private void initFilterButtons()
    {
        ivPlayerFilter = new ImageView(context);
        ivPlayerFilter.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) { if(!showingStreaming) togglePlayerFilter(); }
                return true;
            }
        });
        ViewUtil.initImageView(ivPlayerFilter, ImageView.ScaleType.FIT_XY, false, false, false, (int) (screenWidthOver6 - appState.feedFilterIconHeight/2), Util.pixelNumberForDp(58), 0, 0);
        ivPlayerFilterInverted = new ImageView(context);
        ivPlayerFilterInverted.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) { if(!showingStreaming) togglePlayerFilter(); }
                return true;
            }
        });
        ViewUtil.initImageView(ivPlayerFilterInverted,ImageView.ScaleType.FIT_XY,false,false,false,(int)(screenWidthOver6 - appState.feedFilterIconHeight/2),Util.pixelNumberForDp(58),0,0);

        ivYoutubeFilter = new ImageView(context);
        ivYoutubeFilter.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) { if(!showingStreaming) toggleYoutubeFilter(); }
                return true;
            }
        });
        ViewUtil.initImageView(ivYoutubeFilter,ImageView.ScaleType.FIT_XY,false,false,false,(int)(3*screenWidthOver6 - appState.feedFilterIconHeight/2),Util.pixelNumberForDp(58),0,0);
        ivYoutubeFilterInverted = new ImageView(context);
        ivYoutubeFilterInverted.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) { if(!showingStreaming) toggleYoutubeFilter(); }
                return true;
            }
        });
        ViewUtil.initImageView(ivYoutubeFilterInverted,ImageView.ScaleType.FIT_XY,false,false,false,(int)(3*screenWidthOver6 - appState.feedFilterIconHeight/2),Util.pixelNumberForDp(58),0,0);

        ivTwitchFilter = new ImageView(context);
        ivTwitchFilter.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) { if(!showingStreaming) toggleTwitchFilter(); }
                return true;
            }
        });
        ViewUtil.initImageView(ivTwitchFilter,ImageView.ScaleType.FIT_XY,false,false,false,(int)(5*screenWidthOver6 - appState.feedFilterIconHeight/2),Util.pixelNumberForDp(58),0,0);
        ivTwitchFilterInverted = new ImageView(context);
        ivTwitchFilterInverted.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) { if(!showingStreaming) toggleTwitchFilter(); }
                return true;
            }
        });
        ViewUtil.initImageView(ivTwitchFilterInverted,ImageView.ScaleType.FIT_XY,false,false,false,(int)(5*screenWidthOver6 - appState.feedFilterIconHeight/2),Util.pixelNumberForDp(58),0,0);

    }
    private void initPostDetailGridView()
    {
        gvPostDetail = new GridViewWithOverScroll(context);
        ViewUtil.initGridView(rlPostDetail,gvPostDetail,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,Util.pixelNumberForDp(62));
        gvPostDetail.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        postDetailAdapter = new PostDetailAdapter(context);
        gvPostDetail.setAdapter(postDetailAdapter);

        gvPostDetail.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
//                currentScrollState = scrollState;
//                if (scrollState == OnScrollListener.SCROLL_STATE_FLING)
//                    cancelOverscrollPulldown(gvStreaming);
                gridViewVisibleItemStartIndex = gvPostDetail.getFirstVisiblePosition();
                gridViewVisibleItemEndIndex = gvPostDetail.getLastVisiblePosition();
                appState.reportValidIndexRange(gridViewVisibleItemStartIndex, gridViewVisibleItemEndIndex);
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (gvPostDetail.getVisibility() == View.VISIBLE)
                {
                    gridViewVisibleItemStartIndex = firstVisibleItem;
                    gridViewVisibleItemEndIndex = firstVisibleItem + visibleItemCount;
                    appState.reportValidIndexRange(gridViewVisibleItemStartIndex, gridViewVisibleItemEndIndex);
                }
            }
        });

        gvPostDetail.setOverScrollListener(new GridViewWithOverScroll.OverScrollListener()
        {
            @Override
            public void onOverScroll(int overScrollX, int overScrollY)
            {
                //Log.d("player","overscroll y: " + overScrollY);
//                if(currentScrollState != OnScrollListener.SCROLL_STATE_FLING && overScrollY <= 0)
//                {
//                    vPullDownPercentageBar.setVisibility(View.VISIBLE);
//                    overScrollingUp = true;
//                }
            }
        });
    }
    private void initFeedGridView()
    {
        gvFeed = new GridViewWithOverScroll(context);
        ViewUtil.initGridView(null,gvFeed,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);
        gvFeed.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        feedItemAdapter = new FeedItemAdapter(context);
        gvFeed.setAdapter(feedItemAdapter);
        //Set a scroll listener that will tell the download manager the range of indices to check against before downloading an image. This is to prevent downloading an image that is off screen and might never be seen.
        gvFeed.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                currentScrollState = scrollState;
                if(scrollState == OnScrollListener.SCROLL_STATE_FLING)
                    cancelOverscrollPulldown(gvFeed);
                //if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
                //cancelOverscrollPulldown();
                //if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                //Log.d("player","scroll state TOUCH SCROLL");
                gridViewVisibleItemStartIndex = gvFeed.getFirstVisiblePosition();
                gridViewVisibleItemEndIndex = gvFeed.getLastVisiblePosition();
                appState.reportValidIndexRange(gridViewVisibleItemStartIndex, gridViewVisibleItemEndIndex);
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if(gvFeed.getVisibility() == View.VISIBLE)
                {
                    //if(gvFeed.getChildCount()>0)
                    //Log.d("player","srolled " + gvFeed.getChildAt(0).getTop());
                    gridViewVisibleItemStartIndex = firstVisibleItem;
                    gridViewVisibleItemEndIndex = firstVisibleItem + visibleItemCount;
                    appState.reportValidIndexRange(gridViewVisibleItemStartIndex, gridViewVisibleItemEndIndex);

                    //Log.d("player", "scrolled " + (totalItemCount - firstVisibleItem));

                    //Load more items at the bottom of the list
                    if (totalItemCount > 0 && totalItemCount - firstVisibleItem < 20 && !loadingMoreItemsAtTheBottomOfTheList)
                    {
                        loadMoreFeedItems(feedCursor, 20, false, false, 0);
                        loadingMoreItemsAtTheBottomOfTheList = true;
                    }
                }
            }
        });
        gvFeed.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(refreshingItemList) //While the listview is being cleared and repopulated, don't allow interaction.
                    return true;

                if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                {
                    cancelOverscrollPulldown(gvFeed);
                }
                else if (event.getAction() == MotionEvent.ACTION_DOWN) { }
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    float difference = (event.getY(event.getPointerId(0))-previousTouchY);

                    if(overScrollingUp)
                    {
                        totalOverscrollYLength += difference;

                        if(totalOverscrollYLength<=0)
                            cancelOverscrollPulldown(gvFeed);

                        float pullDownPercentage = totalOverscrollYLength/appState.distanceToOverScrollBeforeRefresh;
                        gvFeed.setTranslationY(2*totalOverscrollYLength/3);
                        vPullDownPercentageBar.setScaleX(pullDownPercentage);
                        if(pullDownPercentage >= 1) //Refresh feed!
                        {
                            refreshFeedItems();
                            cancelOverscrollPulldown(gvFeed);
                        }
                    }

                    previousTouchY = event.getY(event.getPointerId(0));
                }
                return false;
            }
        });
        gvFeed.setOverScrollListener(new GridViewWithOverScroll.OverScrollListener()
        {
            @Override
            public void onOverScroll(int overScrollX, int overScrollY)
            {
                //Log.d("player","overscroll y: " + overScrollY);
                if(currentScrollState != OnScrollListener.SCROLL_STATE_FLING && overScrollY <= 0)
                {
                    vPullDownPercentageBar.setVisibility(View.VISIBLE);
                    overScrollingUp = true;
                }
            }
        });
    }
    private void initStreamGridView()
    {
        gvStreaming = new GridViewWithOverScroll(appState.mainActivity);
        ViewUtil.initGridView(null,gvStreaming,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);
        gvStreaming.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        streamItemAdapter = new StreamItemAdapter(context);
        gvStreaming.setAdapter(streamItemAdapter);
        //Set a scroll listener that will tell the download manager the range of indices to check against before downloading an image. This is to prevent downloading an image that is off screen and might never be seen.
        gvStreaming.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                currentScrollState = scrollState;
                if (scrollState == OnScrollListener.SCROLL_STATE_FLING)
                    cancelOverscrollPulldown(gvStreaming);
                gridViewVisibleItemStartIndex = gvStreaming.getFirstVisiblePosition();
                gridViewVisibleItemEndIndex = gvStreaming.getLastVisiblePosition();
                appState.reportValidIndexRange(gridViewVisibleItemStartIndex, gridViewVisibleItemEndIndex);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (gvStreaming.getVisibility() == View.VISIBLE)
                {
                    gridViewVisibleItemStartIndex = firstVisibleItem;
                    gridViewVisibleItemEndIndex = firstVisibleItem + visibleItemCount;
                    appState.reportValidIndexRange(gridViewVisibleItemStartIndex, gridViewVisibleItemEndIndex);

                    //Load more items at the bottom of the list //TODO - Load more streams when pagination is ready on the server side.
//                    if (totalItemCount > 0 && totalItemCount - firstVisibleItem < 20 && !loadingMoreItemsAtTheBottomOfTheList)
//                    {
//                        loadMoreFeedItems(feedCursor, 20, false, false);
//                        loadingMoreItemsAtTheBottomOfTheList = true;
//                    }
                }
            }
        });
        gvStreaming.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(refreshingItemList) //While the listview is being cleared and repopulated, don't allow interaction.
                    return true;

                if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                {
                    cancelOverscrollPulldown(gvStreaming);
                }
                else if (event.getAction() == MotionEvent.ACTION_DOWN) { }
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    float difference = (event.getY(event.getPointerId(0))-previousTouchY);

                    if(overScrollingUp)
                    {
                        totalOverscrollYLength += difference;

                        if(totalOverscrollYLength<=0)
                            cancelOverscrollPulldown(gvStreaming);

                        float pullDownPercentage = totalOverscrollYLength/appState.distanceToOverScrollBeforeRefresh;
                        gvStreaming.setTranslationY(2*totalOverscrollYLength/3);
                        vPullDownPercentageBar.setScaleX(pullDownPercentage);
                        if(pullDownPercentage >= 1) //Refresh feed!
                        {
                            refreshStreamItems();
                            cancelOverscrollPulldown(gvStreaming);
                        }
                    }

                    previousTouchY = event.getY(event.getPointerId(0));
                }
                return false;
            }
        });
        gvStreaming.setOverScrollListener(new GridViewWithOverScroll.OverScrollListener()
        {
            @Override
            public void onOverScroll(int overScrollX, int overScrollY)
            {
                if(currentScrollState != OnScrollListener.SCROLL_STATE_FLING && overScrollY <= 0)
                {
                    vPullDownPercentageBar.setVisibility(View.VISIBLE);
                    overScrollingUp = true;
                }
            }
        });
    }
    private void showFeed() //This shows the feed gridview and allows the feed filter buttons to show the filter choices.
    {
        if(showingStreaming)
        {
            showingStreaming = false;
            //animateFeedGridBackToVisible();
            animateStreamGridToInvisible();

            if (showingPlayerFeedItems)
            {
                setPlayerFilterDisabled(false);
            }
            if (showingYoutubeFeedItems)
            {
                setYouTubeFilterDisabled(false);
            }
            if (showingTwitchFeedItems)
            {
                setTwitchFilterDisabled(false);
            }
        }
    }
    private void showStreaming() //This hides the feed girdview, shows the stream gridview, and grays out the filter options
    {
        if(!showingStreaming)
        {
            showingStreaming = true;
            //animateStreamGridBackToVisible();
            animateFeedGridToInvisible();

            //grey out the filter buttons. They aren't used for streams
            if (showingPlayerFeedItems)
            {
                setPlayerFilterDisabled(true);
            }
            if (showingYoutubeFeedItems)
            {
                setYouTubeFilterDisabled(true);
            }
            if (showingTwitchFeedItems)
            {
                setTwitchFilterDisabled(true);
            }
        }

    }
    private void setPlayerFilterDisabled(boolean shouldBeDisabled)
    {
        if(shouldBeDisabled)
        {
            ivPlayerFilter.animate().alpha(0f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
            ivPlayerFilterInverted.setVisibility(View.VISIBLE);
            ivPlayerFilterInverted.setAlpha(0f);
            ivPlayerFilterInverted.animate().alpha(0.3f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
        }
        else
        {
            ivPlayerFilterInverted.animate().alpha(0f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
            ivPlayerFilter.setVisibility(View.VISIBLE);
            ivPlayerFilter.setAlpha(0f);
            ivPlayerFilter.animate().alpha(1f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
        }
    }
    private void setYouTubeFilterDisabled(boolean shouldBeDisabled)
    {
        if(shouldBeDisabled)
        {
            ivYoutubeFilter.animate().alpha(0f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
            ivYoutubeFilterInverted.setVisibility(View.VISIBLE);
            ivYoutubeFilterInverted.setAlpha(0f);
            ivYoutubeFilterInverted.animate().alpha(0.3f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
        }
        else
        {
            ivYoutubeFilterInverted.animate().alpha(0f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
            ivYoutubeFilter.setVisibility(View.VISIBLE);
            ivYoutubeFilter.setAlpha(0f);
            ivYoutubeFilter.animate().alpha(1f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
        }
    }
    private void setTwitchFilterDisabled(boolean shouldBeDisabled)
    {
        if(shouldBeDisabled)
        {
            ivTwitchFilter.animate().alpha(0f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
            ivTwitchFilterInverted.setVisibility(View.VISIBLE);
            ivTwitchFilterInverted.setAlpha(0f);
            ivTwitchFilterInverted.animate().alpha(0.3f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
        }
        else
        {
            ivTwitchFilterInverted.animate().alpha(0f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
            ivTwitchFilter.setVisibility(View.VISIBLE);
            ivTwitchFilter.setAlpha(0f);
            ivTwitchFilter.animate().alpha(1f).setDuration(500).setStartDelay(0).setInterpolator(new DecelerateInterpolator(1f));
        }
    }
    private void togglePlayerFilter()
    {
        if(showingPlayerFeedItems)
        {
            if(showingYoutubeFeedItems || showingTwitchFeedItems)
            {
                setPlayerFilterDisabled(true);
                showingPlayerFeedItems = false;
                refreshFeedItems();
            }
        }
        else
        {
            setPlayerFilterDisabled(false);
            showingPlayerFeedItems = true;
            refreshFeedItems();
        }
    }
    private void toggleYoutubeFilter()
    {
        if(showingYoutubeFeedItems)
        {
            if(showingPlayerFeedItems || showingTwitchFeedItems)
            {
                setYouTubeFilterDisabled(true);
                showingYoutubeFeedItems = false;
                refreshFeedItems();
            }
        }
        else
        {
            setYouTubeFilterDisabled(false);
            showingYoutubeFeedItems = true;
            refreshFeedItems();
        }
    }
    private void toggleTwitchFilter()
    {
        if(showingTwitchFeedItems)
        {
            if((showingPlayerFeedItems || showingYoutubeFeedItems))
            {
                setTwitchFilterDisabled(true);
                showingTwitchFeedItems = false;
                refreshFeedItems();
            }
        }
        else
        {
            setTwitchFilterDisabled(false);
            showingTwitchFeedItems = true;
            refreshFeedItems();
        }
    }
    public void selectFollowingFeedSource()
    {
        showingDiscoverFeed = false;
        AnimationManager.translateAndScaleUnderline(vFeedSourceUnderline,(int)(screenWidthOver8*1),followingButtonTextWidth+5);
        showFeed();
        refreshFeedItems();
    }
    public void selectDiscoverFeedSource()
    {
        showingDiscoverFeed = true;
        AnimationManager.translateAndScaleUnderline(vFeedSourceUnderline,(int)(screenWidthOver8*3),discoverButtonTextWidth+5);
        showFeed();
        refreshFeedItems();
    }
    public void selectFollowingStreamingSource()
    {
        showingDiscoverFeed = false;
        AnimationManager.translateAndScaleUnderline(vFeedSourceUnderline,(int)(screenWidthOver8*5),streamingButtonTextWidth+5);
        showStreaming();
        refreshStreamItems();

        //Testing
        //loadMoreStreamItems(-1,100,true,true);
    }
    public void selectDiscoverStreamingSource()
    {
        showingDiscoverFeed = true;
        AnimationManager.translateAndScaleUnderline(vFeedSourceUnderline, (int) (screenWidthOver8 * 7), searchButtonTextWidth + 5);
        showStreaming();
        refreshStreamItems();

        //Testing
        //loadMoreStreamItems(-1,100,true,true);
    }
    private void cancelOverscrollPulldown(GridViewWithOverScroll grid)
    {
        //Log.d("player", "pulldown cancelled");
        vPullDownPercentageBar.setVisibility(View.GONE);
        overScrollingUp = false;
        totalOverscrollYLength = 0;
        grid.animate().translationY(0f).setDuration(TimeMeasurements.ANIMATION_DURATION_PULLDOWN_SNAPBACK).setInterpolator(new BounceInterpolator());
    }
    public void showCreateNewPostWindow(boolean showing)
    {
        if(showing) //Animate the new post dialog onto the screen.
        {
            rlCreateNewPost.setVisibility(View.VISIBLE);
            ivCancelNewPost.setVisibility(View.VISIBLE);
            ivCreateNewPostAvatar.setVisibility(View.VISIBLE);
            tvCreateNewPostUsername.setVisibility(View.VISIBLE);
            ivPostButtonWireFrame.setVisibility(View.VISIBLE);
            rlPostButtonFrame.setVisibility(View.VISIBLE);
            tvPostButtonText.setVisibility(View.VISIBLE);
            etNewPostText.setVisibility(View.VISIBLE);
            tvCharactersRemainingForNewPost.setVisibility(View.VISIBLE);
            vCreateNewPostBottomBar.setVisibility(View.VISIBLE);
            ivCreateNewPostSelectPicture.setVisibility(View.VISIBLE);
            ivCreateNewPostFacebook.setVisibility(View.VISIBLE);
            ivCreateNewPostTwitter.setVisibility(View.VISIBLE);
            rlCreateNewPost.bringToFront();
            Runnable runnableShowKeyboard = new Runnable()
            {
                public void run()
                {
                    InputMethodManager imm = (InputMethodManager) appState.mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etNewPostText, InputMethodManager.SHOW_IMPLICIT);
                }
            };
            AnimationManager.clearTransformationsAndMakeVisible(ivPostButtonWireFrame);
            AnimationManager.clearTransformationsAndMakeVisible(tvPostButtonText);
            AnimationManager.addShowScaleInQuicklyFromLocation(rlCreateNewPost, appState.screenWidth / 2 - Util.pixelNumberForDp(20), -appState.screenWidth / 2 + Util.pixelNumberForDp(50), runnableShowKeyboard);
            AnimationManager.fadeOutPartially(gvFeed, 0.12f);

            appState.backButtonCommands.add(Enums.BackButtonCommand.DISMISS_CREATE_POST_DIALOG);
            if(appState.thisUser != null)
                tvCreateNewPostUsername.setText(appState.thisUser.username);
            ivCancelNewPost.setEnabled(true);
            ivPostButtonWireFrame.setEnabled(true);
            etNewPostText.setEnabled(true);
            ivCreateNewPostFacebook.setEnabled(true);
            ivCreateNewPostTwitter.setEnabled(true);
            etNewPostText.requestFocus();
            appState.mainActivity.menuButton.setEnabled(false);
        }
        else //Close the new post dialog
        {
            appState.base64encodedImageString = ""; //Make sure the next post does not send the same image.
            ivCancelNewPost.setEnabled(false);
            ivPostButtonWireFrame.setEnabled(false);
            ivCreateNewPostFacebook.setEnabled(false);
            ivCreateNewPostTwitter.setEnabled(false);
            Runnable runnableDismissKeyboard = new Runnable()
            {
                public void run()
                {
                    appState.mainActivity.dismissSoftKeyboard();
                    etNewPostText.setEnabled(false);
                    appState.baseContentLayout.requestFocus(); //make the edittext lose focus. Otherwise the soft keyboard tends to randomly pop up.

                    rlCreateNewPost.setVisibility(View.GONE);
                    ivCancelNewPost.setVisibility(View.GONE);
                    ivCreateNewPostAvatar.setVisibility(View.GONE);
                    tvCreateNewPostUsername.setVisibility(View.GONE);
                    ivPostButtonWireFrame.setVisibility(View.GONE);
                    rlPostButtonFrame.setVisibility(View.GONE);
                    tvPostButtonText.setVisibility(View.GONE);
                    etNewPostText.setVisibility(View.GONE);
                    tvCharactersRemainingForNewPost.setVisibility(View.GONE);
                    vCreateNewPostBottomBar.setVisibility(View.GONE);
                    ivCreateNewPostSelectPicture.setVisibility(View.GONE);
                    ivCreateNewPostFacebook.setVisibility(View.GONE);
                    ivCreateNewPostTwitter.setVisibility(View.GONE);

                    appState.mainActivity.menuButton.setEnabled(true);
                }
            };
            AnimationManager.addDismissScaleInQuicklyFromLocation(rlCreateNewPost, appState.screenWidth / 2 - Util.pixelNumberForDp(20), -appState.screenWidth / 2 + Util.pixelNumberForDp(50), runnableDismissKeyboard);
            AnimationManager.fadeInFromAnyAlphaValue(gvFeed);
        }
    }
    public void setAnimationOrigin(float x, float y)
    {
        animationOriginX = x;
        animationOriginY = y;
    }
    public void showPostDetailWindow(boolean showing)
    {
        if(showing)
        {
            rlPostDetail.setVisibility(View.VISIBLE);
            ivClosePostDetail.setVisibility(View.VISIBLE);
            vPostDetailTopBar.setVisibility(View.VISIBLE);
            tvPostDetailTitle.setVisibility(View.VISIBLE);
            vPostDetailTopBarUnderline.setVisibility(View.VISIBLE);
            vPostDetailBottomBar.setVisibility(View.VISIBLE);
            etAddComment.setVisibility(View.VISIBLE);
            tvPostCommentButton.setVisibility(View.VISIBLE);
            AnimationManager.clearTransformationsAndMakeVisible(tvPostCommentButton);
            gvPostDetail.setVisibility(View.VISIBLE);
            postDetailAdapter.notifyDataSetChanged();
            gvPostDetail.setVisibility(View.GONE);

            rlPostDetail.bringToFront();
            appState.backButtonCommands.add(Enums.BackButtonCommand.DISMISS_POST_DETAIL_DIALOG);

            //Disallow multiple clicks on the button
            Runnable runnableShowedPostDetail = new Runnable()
            {
                public void run()
                {
                    appState.uiEnabled = true;
                    AnimationManager.addAnimation(gvPostDetail,AnimationType.SHOW_FADE_IN_QUICKLY);
                }
            };
            appState.uiEnabled = false;
            appState.mainActivity.menuButton.setEnabled(false);
            etAddComment.setEnabled(true);

            AnimationManager.addShowScaleInQuicklyFromLocation(rlPostDetail, animationOriginX - appState.screenWidth / 2, animationOriginY - appState.screenHeight / 2 - 20, runnableShowedPostDetail);
        }
        else
        {
            Runnable runnableDismissedPostDetail = new Runnable()
            {
                public void run()
                {
                    appState.mainActivity.dismissSoftKeyboard();
                    etAddComment.setEnabled(false);
                    appState.baseContentLayout.requestFocus(); //make the edittext lose focus. Otherwise the soft keyboard tends to randomly pop up.
                    feedItemAdapter.notifyDataSetChanged(); //in case the like status changed or the number of comments changed

                    rlPostDetail.setVisibility(View.GONE);
                    ivClosePostDetail.setVisibility(View.GONE);
                    vPostDetailTopBar.setVisibility(View.GONE);
                    tvPostDetailTitle.setVisibility(View.GONE);
                    vPostDetailTopBarUnderline.setVisibility(View.GONE);
                    vPostDetailBottomBar.setVisibility(View.GONE);
                    etAddComment.setVisibility(View.GONE);
                    tvPostCommentButton.setVisibility(View.GONE);
                    gvPostDetail.setVisibility(View.GONE);

                    appState.uiEnabled = true;
                    appState.mainActivity.menuButton.setEnabled(true);
                }
            };
            //Disallow user interaction until the view is closed and its images are cleared.
            appState.uiEnabled = false;
            AnimationManager.addDismissScaleInQuicklyFromLocation(rlPostDetail, animationOriginX - appState.screenWidth / 2, animationOriginY - appState.screenHeight / 2 - 20, runnableDismissedPostDetail);
            //AnimationManager.fadeInFromAnyAlphaValue(gvFeed);
        }
    }

    @Override
    public AppError loadResources(Activity activity)
    {
        try { Thread.sleep(500); } catch (Exception ex) { }

        //Load images from assets
        appState.drwblCreatePost = Util.getResizedDrawableFromAssets(appState.mainActivity, "create_post.png", appState.createNewPostIconHeight,true);
        appState.drwblCreatePostCancel = Util.getResizedDrawableFromAssets(appState.mainActivity, "cancel.png", appState.createNewPostIconHeight,true);

        appState.drwblPlayerFilterIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_player.png", appState.feedFilterIconHeight,true);
        appState.drwblPlayerFilterIconInverted = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_player_inverted.png", appState.feedFilterIconHeight,true);
        appState.drwblPlayerFeedItemIndicator = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_player.png", appState.feedItemTypeIndicatorHeight,true);
        appState.drwblYoutubeFilterIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_youtube.png", appState.feedFilterIconHeight,true);
        appState.drwblYoutubeFilterIconInverted = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_youtube_inverted.png", appState.feedFilterIconHeight,true);
        appState.drwblYoutubeFeedItemIndicator = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_youtube.png", appState.feedItemTypeIndicatorHeight,true);
        appState.drwblTwitchFilterIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_twitch.png", appState.feedFilterIconHeight,true);
        appState.drwblTwitchFilterIconInverted = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_twitch_inverted.png", appState.feedFilterIconHeight,true);
        appState.drwblTwitchFeedItemIndicator = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_twitch.png", appState.feedItemTypeIndicatorHeight,true);

        appState.drwblCommentIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "comment_icon.png", appState.feedItemIconHeight,false);
        appState.drwblEyeIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "eye_icon.png", 2*appState.feedItemIconHeight/3,false);
        appState.drwblLikeIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "like_icon.png", appState.feedItemIconHeight,false);
        appState.drwblLikedIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "liked_icon.png", appState.feedItemIconHeight,false);
        appState.drwblTimeIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "time_icon.png", appState.feedItemIconHeight,true);

        appState.drwblPictureIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "picture_icon.png", appState.createNewPostPictureIconHeight, true);
        appState.drwblTwitterFilterIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "filter_twitter.png",appState.createNewPostPictureIconHeight, true);
        appState.drwblFacebookFilterIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "facebook_icon.png",appState.createNewPostPictureIconHeight, true);

        //Set the listener for the download manager so the feed adapter will receive status updates.
        DownloadManager.getInstance().downloadListener = new DownloadManager.DownloadListener()
        {
            @Override
            public void onDownloadComplete(ImageFromServer imageFromServer)
            {
                for(ImageView imageView : imageFromServer.ivsToBePopulated)
                {
                    imageView.setImageDrawable(imageFromServer.drawable);
                    if (imageFromServer.isAvatar)
                        AnimationManager.addAnimation(imageView, AnimationType.SHOW_SCALE_IN_QUICKLY_WITH_SPIN);
                    else
                        AnimationManager.addAnimation(imageView, AnimationType.SHOW_FADE_IN);
                }
                imageFromServer.ivsToBePopulated.clear(); //the ImageFromServer object no longer needs references to imageViews. They have been populated and animated.
            }
        };

        return AppError.NONE;
    }

    public void loadMoreFeedItems(final long cursor, final int chunkSize, final boolean showLoadingAnimation, final boolean clearCurrentList, final int waitMillies) //wait millies is for delaying this thread
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(waitMillies);
                    itemFetchRequesId++;
                    long requestIdForThisThread = itemFetchRequesId;

                    //Log.d("player", "waiting for feed item list lock");
                    synchronized (feedItemListLock)
                    {
                        if (requestIdForThisThread != itemFetchRequesId || downloadsCancelled)
                        {
                            Log.d("player", "acquired feed item list lock - request rejected");
                        }
                        else
                        {
                            //Log.d("player", "acquired feed item list lock - getting more feed items from the server");
                            if (clearCurrentList)
                                clearFeedItems();
                            if (showLoadingAnimation)
                                appState.mainActivity.showLoadingAnimation();

                            ArrayList<FeedItem> newFeedItems = ApiMethods.getFeedItems(chunkSize, cursor, showingPlayerFeedItems, showingYoutubeFeedItems, showingTwitchFeedItems, showingDiscoverFeed);

                            if (newFeedItems.size() > 0)
                            {
                                feedCursor = newFeedItems.size() > 0 ? newFeedItems.get(newFeedItems.size() - 1).id : 0; //Post id is the cursor
                                processFeedItems(newFeedItems);
                                for (FeedItem newFeedItem : newFeedItems)
                                    appState.fetchedFeedItems.add(newFeedItem);
                                newFeedItems.clear();
                            }
                            else if (appState.loginError == LoginError.BAD_CREDENTIALS) //In case our access token expired before starting this call.
                            {
                                if (!attemptToRefreshAccessToken()) //Try to use the refresh token to get a new access token.
                                    downloadsCancelled = true;
                                else //Got a new access token. Try fetching again
                                    loadMoreFeedItems(cursor, chunkSize, showLoadingAnimation, clearCurrentList, 0);
                            }

                            refreshFeedItemGrid();
                            if (showLoadingAnimation)
                                appState.mainActivity.dismissLoadingAnimation();
                            if (gvFeed.getVisibility() == View.GONE && !showingStreaming) //The second condition is for the case when the user clicked on a button to show streams rather than feed while the feed was in the middle of loading.
                                animateFeedGridBackToVisible();
                            //Done loading - Allow other methods to once again initiate requests to load more.
                            refreshingItemList = false;
                            loadingMoreItemsAtTheBottomOfTheList = false;
                            if(clearCurrentList) //If we were clearing the list, then this is a refresh, and we should scroll to the top.
                                gvFeed.smoothScrollToPosition(0);
                            AnimationManager.showingLoadingAnimation = false; //This is to stop the now-out-of-view loading animation at the bottom of the grid from looping indefinitely.
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }).start();
        //}
    }
    public void loadMoreStreamItems(final long cursor, final int chunkSize, final boolean showLoadingAnimation, final boolean clearCurrentList, final int waitMillies)
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(waitMillies);
                    itemFetchRequesId++;
                    long requestIdForThisThread = itemFetchRequesId;

                    //Log.d("player", "waiting for feed item list lock");
                    synchronized (feedItemListLock)
                    {
                        if (requestIdForThisThread != itemFetchRequesId || downloadsCancelled)
                        {
                            //Log.d("player", "acquired feed item list lock - request rejected");
                        }
                        else
                        {
                            //Log.d("player", "acquired feed item list lock - getting more feed items from the server");
                            if (clearCurrentList)
                                clearStreamItems();
                            if (showLoadingAnimation)
                                appState.mainActivity.showLoadingAnimation();

                            ArrayList<StreamItem> newStreamItems = ApiMethods.getStreamItems(100, 1, showingDiscoverFeed);

                            if (newStreamItems.size() > 0)
                            {
                                //feedCursor = newFeedItems.size() > 0 ? newFeedItems.get(newFeedItems.size() - 1).id : 0; //Post id is the cursor
                                processStreamItems(newStreamItems);
                                for (StreamItem newStreamItem : newStreamItems)
                                    appState.fetchedStreamItems.add(newStreamItem);
                                newStreamItems.clear();
                            }
                            else if (appState.loginError == LoginError.BAD_CREDENTIALS) //In case our access token expired before starting this call.
                            {
                                if (!attemptToRefreshAccessToken()) //Try to use the refresh token to get a new access token.
                                    downloadsCancelled = true;
                                else //Got a new access token. Try fetching again
                                    loadMoreStreamItems(cursor, chunkSize, showLoadingAnimation, clearCurrentList, 0);
                            }

                            refreshStreamItemGrid();
                            if (showLoadingAnimation)
                                appState.mainActivity.dismissLoadingAnimation();
                            if (gvStreaming.getVisibility() == View.GONE && showingStreaming) //The second condition is for the case when the user clicked on a button to show feed rather than streams while streams were in the middle of loading.
                                animateStreamGridBackToVisible();
                            //Done loading - Allow other methods to once again initiate requests to load more.
                            refreshingItemList = false;
                            loadingMoreItemsAtTheBottomOfTheList = false;
                            //if(!downloadsCancelled)
                            AnimationManager.showingLoadingAnimation = false; //This is to stop the now-out-of-view loading animation at the bottom of the grid from looping indefinitely.
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }).start();
        //}
    }
    private void animateFeedGridBackToVisible()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                gvFeed.setVisibility(View.VISIBLE);
                gvFeed.animate().alpha(1f).setDuration(500).setInterpolator(new DecelerateInterpolator(1f));
            }
        });
    }
    private void animateFeedGridToInvisible()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                Runnable makeFeedGone = new Runnable() { public void run() { gvFeed.setVisibility(View.GONE); } };
                gvFeed.animate().alpha(0f).setDuration(500).setInterpolator(new DecelerateInterpolator(1f)).withEndAction(makeFeedGone);
            }
        });
    }
    private void animateStreamGridBackToVisible()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                gvStreaming.setVisibility(View.VISIBLE);
                gvStreaming.animate().alpha(1f).setDuration(500).setInterpolator(new DecelerateInterpolator(1f));
            }
        });
    }
    private void animateStreamGridToInvisible()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                Runnable makeStreamGone = new Runnable() { public void run() { gvStreaming.setVisibility(View.GONE); } };
                gvStreaming.animate().alpha(0f).setDuration(500).setInterpolator(new DecelerateInterpolator(1f)).withEndAction(makeStreamGone);
            }
        });
    }
    private void cancelFlingOnGrids()
    {
        gvFeed.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
        gvStreaming.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }

    private void refreshFeedItems()
    {
        //Stop a fling if it is in progress.
        cancelFlingOnGrids();

        refreshingItemList = true;
        gvFeed.animate().cancel();
        gvFeed.animate().alpha(0f).setDuration(500).setInterpolator(new DecelerateInterpolator(1f)).withEndAction(new Runnable() {
            @Override
            public void run()
            {
                gvFeed.setVisibility(View.GONE);
            }
        });
        loadMoreFeedItems(-1,50,true,true,500);
    }
    private void refreshStreamItems()
    {
        //Stop a fling if it is in progress.
        cancelFlingOnGrids();

        refreshingItemList = true;
        gvStreaming.animate().cancel();
        gvStreaming.animate().alpha(0f).setDuration(500).setInterpolator(new DecelerateInterpolator(1f)).withEndAction(new Runnable() {
            @Override
            public void run()
            {
                gvStreaming.setVisibility(View.GONE);
            }
        });
        loadMoreStreamItems(-1,50,true,true,500);
    }
    private void refreshFeedItemGrid()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                try { feedItemAdapter.notifyDataSetChanged(); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }
    private void refreshStreamItemGrid()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                try { streamItemAdapter.notifyDataSetChanged(); }
                catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }
    private void processFeedItems(ArrayList<FeedItem> feedItems)
    { //Here, we do things like set the "time ago" text since the item from the server gave us a date, and append important bits to url strings.
        for(int i=0;i<feedItems.size();i++)
        {
            FeedItem feedItem = feedItems.get(i);
            FeedItemData feedItemData = feedItem.data;

            feedItemData.thumbnailImageFromServer = new ImageFromServer();
            feedItemData.thumbnailImageFromServer.url = "https:" + feedItemData.thumbnailUrl.replace(".jpg",appState.feedItemThumbnailResize + ".jpg");

            feedItemData.avatarImageFromServer = new ImageFromServer();
            feedItemData.avatarImageFromServer.url = "https:" + feedItem.user.avatarUrl.replace(".jpg", appState.feedItemAvatarResize + ".jpg");

            if(feedItemData.hasPostedImageThumbnail)
            {
                FeedItemDataMeta feedItemDataMeta = feedItemData.metas.get(0);
                feedItemDataMeta.thumbnailImageFromServer = new ImageFromServer();
                feedItemDataMeta.thumbnailImageFromServer.url = "https:" + feedItemDataMeta.thumbnail.replace(".jpg", appState.feedItemThumbnailResize + ".jpg");
                if(!feedItemDataMeta.url.toLowerCase().contains("http"))
                    feedItemDataMeta.url = "https:" + feedItemDataMeta.url;
            }

            feedItem.timeAgo = Util.GetTimeagoStringByDate(feedItem.publishedDate, true);
        }
    }
    private void processStreamItems(ArrayList<StreamItem> streamItems)
    {
        for(int i=0;i<streamItems.size();i++)
        {
            StreamItem streamItem = streamItems.get(i);

            streamItem.thumbnailImageFromServer = new ImageFromServer();
            streamItem.thumbnailImageFromServer.url = streamItem.thumbnail; //"https:" + streamItem.thumbnail.replace(".jpg",appState.feedItemThumbnailResize + ".jpg");

            streamItem.avatarImageFromServer = new ImageFromServer();
            streamItem.avatarImageFromServer.url = "https:" + streamItem.user.avatarUrl.replace(".jpg", appState.streamItemAvatarResize + ".jpg");

            double thousandsOfViews = streamItem.data.viewers/1000;
            if(thousandsOfViews>1) streamItem.viewersString = Integer.toString((int)thousandsOfViews) + "K";
            else streamItem.viewersString = Integer.toString((int)streamItem.data.viewers);
        }
    }


    @Override
    public AppError show()
    {
        //reset filters
        showingYoutubeFeedItems = true;
        showingPlayerFeedItems = true;
        showingTwitchFeedItems = true;
        showingDiscoverFeed = false;
        showingStreaming = false;

        appState.mainActivity.showMenuButton();
        AnimationManager.addAnimation(rlGradient, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(rlTitleBar, AnimationType.SHOW_ENTER_FROM_TOP);
        AnimationManager.addAnimation(tvTitle, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(vTitleBarUnderline, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(vSourcesBackground, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(vFeedTypeBackground, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(tvFollowingButton, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(tvDiscoverButton, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(tvStreamingButton, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.addAnimation(tvSearchButton, AnimationType.SHOW_FADE_IN_LATE);
        AnimationManager.showUnderline(vFeedSourceUnderline,(int)screenWidthOver8,followingButtonTextWidth+5);

        ivCreatePost.setImageDrawable(appState.drwblCreatePost);
        AnimationManager.addAnimation(ivCreatePost, AnimationType.SHOW_FADE_IN_FROM_RIGHT);
        ivCancelNewPost.setImageDrawable(appState.drwblCreatePostCancel);
        ivClosePostDetail.setImageDrawable(appState.drwblCreatePostCancel);
        ivCreateNewPostAvatar.setImageDrawable(appState.currentUserCircularAvatar);
        ivCreateNewPostSelectPicture.setImageDrawable(appState.drwblPictureIcon);
        ivCreateNewPostFacebook.setImageDrawable(appState.drwblFacebookFilterIcon);
        ivCreateNewPostTwitter.setImageDrawable(appState.drwblTwitterFilterIcon);

        ivPlayerFilter.setImageDrawable(appState.drwblPlayerFilterIcon);
        ivPlayerFilterInverted.setImageDrawable(appState.drwblPlayerFilterIconInverted);
        ivPlayerFilterInverted.setScaleX(1.0f);ivPlayerFilterInverted.setScaleY(1.0f);
        AnimationManager.addAnimation(ivPlayerFilter,AnimationType.SHOW_SCALE_IN_RANDOMLY_VERY_LATE);

        ivYoutubeFilter.setImageDrawable(appState.drwblYoutubeFilterIcon);
        ivYoutubeFilterInverted.setImageDrawable(appState.drwblYoutubeFilterIconInverted);
        ivYoutubeFilterInverted.setScaleX(1.0f);ivYoutubeFilterInverted.setScaleY(1.0f);
        AnimationManager.addAnimation(ivYoutubeFilter,AnimationType.SHOW_SCALE_IN_RANDOMLY_VERY_LATE);

        ivTwitchFilter.setImageDrawable(appState.drwblTwitchFilterIcon);
        ivTwitchFilterInverted.setImageDrawable(appState.drwblTwitchFilterIconInverted);
        ivTwitchFilterInverted.setScaleX(1.0f);ivTwitchFilterInverted.setScaleY(1.0f);
        AnimationManager.addAnimation(ivTwitchFilter,AnimationType.SHOW_SCALE_IN_RANDOMLY_VERY_LATE);

        //Start first thread to get feed items
        refreshingItemList = true;
        loadMoreFeedItems(-1,50,true,true,0); //-1 means do not include cursor in request parameters

        //Allow downloads - (They might have been disabled on the last visit to this page.)
        downloadsCancelled = false;

        return AppError.NONE;
    }

    @Override
    public AppError dismiss()
    {
        downloadsCancelled = true; //Stop any pending downloads.

        appState.mainActivity.dismissMenuButton();
        vPullDownPercentageBar.setVisibility(View.GONE);
        AnimationManager.addAnimation(gvFeed, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(gvStreaming, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(rlGradient, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(rlTitleBar,AnimationType.DISMISS_EXIT_THROUGH_TOP);
        AnimationManager.addAnimation(tvTitle, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(ivCreatePost,AnimationType.DISMISS_FADE_OUT_RIGHT);
        AnimationManager.addAnimation(vTitleBarUnderline, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(vSourcesBackground, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(vFeedTypeBackground, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(tvFollowingButton, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(tvDiscoverButton, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(tvStreamingButton, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(tvSearchButton, AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(vFeedSourceUnderline, AnimationType.DISMISS_SCALE_OUT_SPIN_RANDOMLY);

        if(ivPlayerFilter.getVisibility() != View.GONE)
            AnimationManager.addAnimation(ivPlayerFilter, AnimationType.DISMISS_FADE_OUT_SHRINK);
        if(ivPlayerFilterInverted.getVisibility() != View.GONE)
            AnimationManager.addAnimation(ivPlayerFilterInverted, AnimationType.DISMISS_FADE_OUT_SHRINK);
        if(ivYoutubeFilter.getVisibility() != View.GONE)
            AnimationManager.addAnimation(ivYoutubeFilter, AnimationType.DISMISS_FADE_OUT_SHRINK);
        if(ivYoutubeFilterInverted.getVisibility() != View.GONE)
            AnimationManager.addAnimation(ivYoutubeFilterInverted, AnimationType.DISMISS_FADE_OUT_SHRINK);
        if(ivTwitchFilter.getVisibility() != View.GONE)
            AnimationManager.addAnimation(ivTwitchFilter, AnimationType.DISMISS_FADE_OUT_SHRINK);
        if(ivTwitchFilterInverted.getVisibility() != View.GONE)
            AnimationManager.addAnimation(ivTwitchFilterInverted, AnimationType.DISMISS_FADE_OUT_SHRINK);

        return AppError.NONE;
    }

    private void clearFeedItems()
    {
//        for(int i=0;i<appState.fetchedFeedItems.size();i++)
//        {
//            FeedItem feedItem = appState.fetchedFeedItems.get(i);
//            feedItem.data.thumbnailImageFromServer.drawable = null;
//            feedItem.data.avatarImageFromServer.drawable = null;
//        }
        appState.fetchedFeedItems.clear();
    }
    private void clearStreamItems()
    {
//        for(int i=0;i<appState.fetchedStreamItems.size();i++)
//        {
//            StreamItem streamItem = appState.fetchedStreamItems.get(i);
//            streamItem.thumbnailImageFromServer.drawable = null;
//            streamItem.avatarImageFromServer.drawable = null;
//        }
        appState.fetchedStreamItems.clear();
    }

    @Override
    public AppError releaseResourcesAndHideAllViews()
    {
        //Null all drawables associated with feed items or stream items
        new Thread(new Runnable()
        {
            public void run()
            {
                synchronized (feedItemListLock)
                {
                    clearFeedItems();
                    clearStreamItems();
                }
            }
        }).start();

        rlGradient.setVisibility(View.GONE);
        rlTitleBar.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        vTitleBarUnderline.setVisibility(View.GONE);
        vSourcesBackground.setVisibility(View.GONE);
        vFeedTypeBackground.setVisibility(View.GONE);
        tvFollowingButton.setVisibility(View.GONE);
        tvDiscoverButton.setVisibility(View.GONE);
        tvStreamingButton.setVisibility(View.GONE);
        tvSearchButton.setVisibility(View.GONE);
        gvFeed.setVisibility(View.GONE);
        gvStreaming.setVisibility(View.GONE);

        ivCreatePost.setImageDrawable(null);
        appState.drwblCreatePost = null;
        ivCreatePost.setVisibility(View.GONE);
        ivClosePostDetail.setImageDrawable(null);
        ivClosePostDetail.setVisibility(View.GONE);
        ivCancelNewPost.setImageDrawable(null);
        appState.drwblCreatePostCancel = null;
        ivCancelNewPost.setVisibility(View.GONE);
        ivCreateNewPostAvatar.setImageDrawable(null);
        ivCreateNewPostAvatar.setVisibility(View.GONE);
        tvCreateNewPostUsername.setVisibility(View.GONE);

        //Filter icons
        ivPlayerFilter.setImageDrawable(null);
        appState.drwblPlayerFilterIcon = null;
        ivPlayerFilter.setVisibility(View.GONE);
        ivPlayerFilterInverted.setImageDrawable(null);
        appState.drwblPlayerFilterIconInverted = null;
        ivPlayerFilterInverted.setVisibility(View.GONE);

        ivCancelNewPost.setImageDrawable(null);
        ivCreateNewPostFacebook.setImageDrawable(null);
        ivCreateNewPostTwitter.setImageDrawable(null);
        ivCancelNewPost.setVisibility(View.GONE);
        ivCreateNewPostFacebook.setVisibility(View.GONE);
        ivCreateNewPostTwitter.setVisibility(View.GONE);
        ivCreateNewPostSelectPicture.setImageDrawable(null);
        ivCreateNewPostSelectPicture.setVisibility(View.GONE);
        appState.drwblPictureIcon = null;
        appState.drwblFacebookFilterIcon = null;
        appState.drwblTwitterFilterIcon = null;

        ivYoutubeFilter.setImageDrawable(null);
        appState.drwblYoutubeFilterIcon = null;
        ivYoutubeFilter.setVisibility(View.GONE);
        ivYoutubeFilterInverted.setImageDrawable(null);
        appState.drwblYoutubeFilterIconInverted = null;
        ivYoutubeFilterInverted.setVisibility(View.GONE);

        ivTwitchFilter.setImageDrawable(null);
        appState.drwblTwitchFilterIcon = null;
        ivTwitchFilter.setVisibility(View.GONE);
        ivTwitchFilterInverted.setImageDrawable(null);
        appState.drwblTwitchFilterIconInverted = null;
        ivTwitchFilterInverted.setVisibility(View.GONE);

        appState.drwblCommentIcon = null;
        appState.drwblLikeIcon = null;
        appState.drwblLikedIcon = null;
        appState.drwblTimeIcon = null;
        appState.drwblEyeIcon = null;
        appState.drwblCreatePostCancel = null;

        return AppError.NONE;
    }
}

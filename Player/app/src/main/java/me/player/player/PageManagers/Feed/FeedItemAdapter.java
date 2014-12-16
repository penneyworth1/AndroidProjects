package me.player.player.PageManagers.Feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
//import android.text.method.LinkMovementMethod;
import android.os.Handler;
import android.os.Looper;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.player.player.AnimationManager;
import me.player.player.Api.ApiMethods;
import me.player.player.Api.DownloadManager;
import me.player.player.AppState;
import me.player.player.Constants.Enums;
import me.player.player.Entities.FeedItem;
import me.player.player.Entities.FeedItemData;
import me.player.player.Entities.FeedItemDataMeta;
import me.player.player.Entities.ImageFromServer;
import me.player.player.Entities.User;
import me.player.player.Util;
import me.player.player.ViewUtil;
import me.player.player.Constants.Enums.FeedType;


/**
 * Created by stevenstewart on 9/11/14.
 */
public class FeedItemAdapter extends BaseAdapter
{
    Context context;
    AppState appState = AppState.getInstance();
    boolean noItems = false;

    public FeedItemAdapter(Context contextPar)
    {
        context = contextPar;
    }

    static class FeedItemHolder
    {
        View vMainContentBackground;
        View vBottomBarBackground;
        View vRightColorBar;
        ImageView ivAvatar;
        ImageView ivVideoThumbnail;
        ImageView ivSourceIndicator;
        ImageView ivTimeIcon;
        TextView tvTimeAgo;
        ImageView ivCommentIcon;
        TextView tvCommentCount;
        ImageView ivLikeIcon;
        TextView tvLikeCount;
        TextView tvUsername;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPost;

        //Metas
        ImageView ivPostedImageThumbnail;
        TextView tvMetaTitle;
        RelativeLayout rlMetaItemContainer;

        RelativeLayout rlInnerContainer; //This is the true container for the feed item, but it must be wrapped in a linear layout for various annoying reasons such as the fact that a relative layout cannot wrap content vertically and also maintain margins and background color. There are other horribly nasty side effects of trying to let a relative layout grow naturally with wrap_content.
    }

    public int getCount()
    {
        if (appState.fetchedFeedItems.size() == 0)
            noItems = true;
        else
            noItems = false;

        return appState.fetchedFeedItems.size() + 2;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            if (position == 0) //Position zero should return a blank, transparent layout because this will initially be behind the menu and not fully visible.
            {
                //return getBlankRelativeLayout();
                return ViewUtil.getBlankLinearLayoutForListView(context);
            }
            if(position == 1 && noItems) //When there are no items, show a message.
            {
                return ViewUtil.getNothingHereListviewItem(context);
            }
            if (position == (appState.fetchedFeedItems.size()+1)) //Case where we should show the loading animation at the bottom of the screen
            {
                if(appState.adapterIndexRangeStart > 0)
                {
                    LinearLayout loadingLinearLayout = new LinearLayout(context);
                    AbsListView.LayoutParams loadingLayoutParams = new AbsListView.LayoutParams(appState.screenWidth, AbsListView.LayoutParams.WRAP_CONTENT);
                    loadingLinearLayout.setLayoutParams(loadingLayoutParams);
                    initLoadingAnimationGraphics(loadingLinearLayout);
                    return loadingLinearLayout;
                }
                else
                    return ViewUtil.getBlankLinearLayoutForListView(context);
            }

            LinearLayout baseLayout = (LinearLayout) convertView;
            final FeedItemHolder feedItemHolder;

            if (baseLayout == null || baseLayout.getTag() == null) //The second condition is for when a view is converted from the blank layout from position 0.
            {
                baseLayout = new LinearLayout(context);
                feedItemHolder = new FeedItemHolder();
                baseLayout.setTag(feedItemHolder);
                initFeedItemLayout(baseLayout, feedItemHolder);
            }
            else
            {
                feedItemHolder = (FeedItemHolder) baseLayout.getTag();
            }

            final FeedItem feedItem = appState.fetchedFeedItems.get(position-1);
            FeedItemData feedItemData = feedItem.data;
            User user = feedItem.user;

            //Choose the height of the item based on whether or not it has a video thumbnail.
            if (feedItemData.thumbnailUrl.length() > 0)
                baseLayout.getLayoutParams().height = appState.feedItemHeight;
            else
                baseLayout.getLayoutParams().height = AbsListView.LayoutParams.WRAP_CONTENT;

            //Set text elements of this feed item display
            feedItemHolder.tvUsername.setText(user.username);
            feedItemHolder.tvTimeAgo.setText(feedItem.timeAgo);
            feedItemHolder.tvCommentCount.setText(Integer.toString(feedItem.commentCount));
            feedItemHolder.tvLikeCount.setText(Integer.toString(feedItem.likesCount));
            if(feedItem.hasLiked)
                feedItemHolder.ivLikeIcon.setImageDrawable(appState.drwblLikedIcon);
            else
                feedItemHolder.ivLikeIcon.setImageDrawable(appState.drwblLikeIcon);
            feedItemHolder.ivLikeIcon.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled && feedItem.user.id != appState.thisUser.id)
                    {
                        if(feedItem.hasLiked)
                        {
                            feedItem.hasLiked = false;
                            feedItem.likesCount--;
                            feedItemHolder.ivLikeIcon.setImageDrawable(appState.drwblLikeIcon);
                        }
                        else
                        {
                            feedItem.hasLiked = true;
                            feedItem.likesCount++;
                            feedItemHolder.ivLikeIcon.setImageDrawable(appState.drwblLikedIcon);
                        }
                        notifyDataSetChanged();
                        new Thread(new Runnable() { public void run() { ApiMethods.likeFeedItem(feedItem.hasLiked,Long.toString(feedItem.id)); } }).start();
                    }
                    return true;
                }
            });

            //TODO
            //Set tags for the image views to know what index they are currently being shown for
            //feedItemHolder.ivVideoThumbnail.setTag(position);
            //feedItemHolder.ivAvatar.setTag(position);

            //Alter the layout based on feed type and feed source
            //---------------------------------------------------------------------------------
            if (feedItem.type == FeedType.VIDEO)
            {
                feedItemHolder.ivVideoThumbnail.setVisibility(View.VISIBLE);
                feedItemHolder.tvTitle.setVisibility(View.VISIBLE);
                feedItemHolder.tvDescription.setVisibility(View.VISIBLE);
                feedItemHolder.tvPost.setVisibility(View.GONE);
                feedItemHolder.tvTitle.setText(feedItemData.title);
            }
            else if (feedItem.type == FeedType.POST)
            {
                feedItemHolder.tvPost.setVisibility(View.VISIBLE);
                feedItemHolder.tvTitle.setVisibility(View.GONE);
                feedItemHolder.tvDescription.setVisibility(View.GONE);
                feedItemHolder.ivVideoThumbnail.setVisibility(View.GONE);
                feedItemHolder.tvPost.setAutoLinkMask(Linkify.ALL); //Detect links and make them clickable automatically.
                feedItemHolder.tvPost.setText(feedItemData.postRaw);
                feedItemHolder.tvPost.setLinkTextColor(Color.parseColor("#9999FF"));
            }
            if (feedItem.source == Enums.FeedFilter.PLAYERME)
            {
                feedItemHolder.ivSourceIndicator.setImageDrawable(appState.drwblPlayerFeedItemIndicator);
                feedItemHolder.vRightColorBar.setBackgroundColor(Color.parseColor("#17B5BD"));
            }
            else if (feedItem.source == Enums.FeedFilter.YOUTUBE)
            {
                feedItemHolder.ivSourceIndicator.setImageDrawable(appState.drwblYoutubeFeedItemIndicator);
                feedItemHolder.vRightColorBar.setBackgroundColor(Color.parseColor("#C21F1E"));
            }
            else if (feedItem.source == Enums.FeedFilter.TWITCH)
            {
                feedItemHolder.ivSourceIndicator.setImageDrawable(appState.drwblTwitchFeedItemIndicator);
                feedItemHolder.vRightColorBar.setBackgroundColor(Color.parseColor("#502B94"));
            }
            //---------------------------------------------------------------------------------

            //Whenever an image is to be shown on screen, set its "importance" to 1 so that it is now last on the list to be recycled.
            feedItemData.avatarImageFromServer.importance = 1;
            feedItemData.thumbnailImageFromServer.importance = 1;

            //Avatar image downloading and displaying
            feedItemHolder.ivAvatar.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
            if (feedItem.user.avatarUrl.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(feedItemData.avatarImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
            {
                feedItemData.avatarImageFromServer.isAvatar = true; //This lets the download manager know to circle crop the image.
                feedItemData.avatarImageFromServer.mostRecentAdapterIndex = position;
                ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(feedItemData.avatarImageFromServer);
                if (duplicate != null)
                {
                    feedItemData.avatarImageFromServer = duplicate;
                    if (duplicate.drawable == null)
                        feedItemData.avatarImageFromServer.ivsToBePopulated.add(feedItemHolder.ivAvatar); //This is the case when a duplicate avatar is seen on screen before the first one gets a chance to donwload.
                }
                else //If there is no duplicate, we should always add the imageView to the to-be-animated list.
                {
                    feedItemData.avatarImageFromServer.ivsToBePopulated.add(feedItemHolder.ivAvatar);
                }
            }
            if (feedItemData.avatarImageFromServer.drawable != null) //see if the drawable is ready.
            {
                feedItemHolder.ivAvatar.setImageDrawable(feedItemData.avatarImageFromServer.drawable);
                feedItemHolder.ivAvatar.setVisibility(View.VISIBLE);
            }

            //Video Thumbnail image downloading and displaying
            feedItemHolder.ivVideoThumbnail.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
            if (feedItemData.thumbnailUrl.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(feedItemData.thumbnailImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
            {
                feedItemData.thumbnailImageFromServer.mostRecentAdapterIndex = position;
                ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(feedItemData.thumbnailImageFromServer);
                if (duplicate != null)
                {
                    feedItemData.thumbnailImageFromServer = duplicate;
                    if (duplicate.drawable == null)
                        feedItemData.thumbnailImageFromServer.ivsToBePopulated.add(feedItemHolder.ivVideoThumbnail); //This is the case when a duplicate is seen on screen before the first one gets a chance to donwload.
                }
                else //The there is no duplicate, we should always add the imageView to the to-be-animated list.
                {
                    feedItemData.thumbnailImageFromServer.ivsToBePopulated.add(feedItemHolder.ivVideoThumbnail);
                }
            }
            if (feedItemData.thumbnailImageFromServer.drawable != null) //The download manager contains this image, see if its drawable is ready.
            {
                feedItemHolder.ivVideoThumbnail.setImageDrawable(feedItemData.thumbnailImageFromServer.drawable);
                feedItemHolder.ivVideoThumbnail.setVisibility(View.VISIBLE);
            }

            //Posted image thumbnail downloading and displaying
            if(feedItemData.hasPostedImageThumbnail)
            {
                feedItemHolder.ivPostedImageThumbnail.setVisibility(View.VISIBLE);
                final FeedItemDataMeta feedItemDataMeta = feedItemData.metas.get(0);
                feedItemHolder.ivPostedImageThumbnail.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
                if (!(DownloadManager.getInstance().imagesFromServer.contains(feedItemDataMeta.thumbnailImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
                {
                    feedItemDataMeta.thumbnailImageFromServer.mostRecentAdapterIndex = position;
                    ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(feedItemDataMeta.thumbnailImageFromServer);
                    if (duplicate != null)
                    {
                        feedItemDataMeta.thumbnailImageFromServer = duplicate;
                        if (duplicate.drawable == null)
                            feedItemDataMeta.thumbnailImageFromServer.ivsToBePopulated.add(feedItemHolder.ivPostedImageThumbnail); //This is the case when a duplicate is seen on screen before the first one gets a chance to donwload.
                    }
                    else //The there is no duplicate, we should always add the imageView to the to-be-animated list.
                    {
                        feedItemDataMeta.thumbnailImageFromServer.ivsToBePopulated.add(feedItemHolder.ivPostedImageThumbnail);
                    }
                }
                if (feedItemDataMeta.thumbnailImageFromServer.drawable != null) //The download manager contains this image, see if its drawable is ready.
                {
                    feedItemHolder.ivPostedImageThumbnail.setImageDrawable(feedItemDataMeta.thumbnailImageFromServer.drawable);

                }
                feedItemHolder.rlMetaItemContainer.setVisibility(View.VISIBLE);
                if(feedItemDataMeta.title.length()>0 && !feedItemDataMeta.title.equalsIgnoreCase("null"))
                {
                    feedItemHolder.tvMetaTitle.setVisibility(View.VISIBLE);
                    feedItemHolder.tvMetaTitle.setAutoLinkMask(Linkify.ALL);
                    feedItemHolder.tvMetaTitle.setText(feedItemDataMeta.title);
                }  else { feedItemHolder.tvMetaTitle.setText(""); }

                feedItemHolder.tvPost.setPadding(0, 0, 0, appState.feedItemBottomBarHeight + Util.pixelNumberForDp(10) + appState.feedItemThumbnailHeight);
                //Set tap interaction with meta item thumbnail.
                feedItemHolder.ivPostedImageThumbnail.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if(appState.uiEnabled)
                        {
                            if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                            {
                                try
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedItemDataMeta.url));
                                    appState.mainActivity.startActivity(intent);
                                } catch (Exception ex)
                                {
                                    Toast.makeText(appState.mainActivity.getApplicationContext(), "Unable to find an app to open this link!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        return true;
                    }
                });
            }
            else //Don't leave extra space for the thumbnail that won't be shown. Hide views that would show meta items.
            {
                feedItemHolder.tvPost.setPadding(0, 0, 0, appState.feedItemBottomBarHeight + Util.pixelNumberForDp(5));
                feedItemHolder.ivPostedImageThumbnail.setVisibility(View.GONE);
                feedItemHolder.rlMetaItemContainer.setVisibility(View.GONE);
                feedItemHolder.tvMetaTitle.setVisibility(View.GONE);
            }

            //Add tap interaction to video link.
            if (feedItem.type == FeedType.VIDEO)
            {
                feedItemHolder.ivVideoThumbnail.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if(appState.uiEnabled)
                        {
                            if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                            {
                                try
                                {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedItem.sourceUrl));
                                    appState.mainActivity.startActivity(intent);
                                } catch (Exception ex)
                                {
                                    Toast.makeText(appState.mainActivity.getApplicationContext(), "Unable to find an app to open this video.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        return true;
                    }
                });
            }
            //Add tap interaction to the comments bubble graphic. This will open the detail view of the post with comments.
            feedItemHolder.ivCommentIcon.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                    {
                        appState.selectedFeedItem = feedItem;
                        appState.numberOfCommentsShowing = feedItem.comments.size();
                        appState.mainActivity.feedPageManager.setAnimationOrigin(event.getRawX(),event.getRawY());
                        appState.mainActivity.feedPageManager.showPostDetailWindow(true);
                        appState.reportValidIndexRange(0, 20); //Make sure initial items get downloaded.
                    }
                    return true;
                }
            });

            return baseLayout;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            //the grid needs some kind of view to be returned
            return ViewUtil.getBlankLinearLayoutForListView(context);
        }
    }

    private void initFeedItemLayout(LinearLayout baseLayout, FeedItemHolder feedItemHolder)
    {
        baseLayout.setPadding(0,appState.feedItemMarginTop,appState.feedItemMarginRight,0);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(appState.screenWidth, AbsListView.LayoutParams.WRAP_CONTENT);// appState.feedItemHeight);
        baseLayout.setLayoutParams(layoutParams);

        feedItemHolder.rlInnerContainer = new RelativeLayout(context);
        LinearLayout.LayoutParams innerContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        baseLayout.addView(feedItemHolder.rlInnerContainer,innerContainerParams);

        feedItemHolder.vMainContentBackground = new View(context);
        ViewUtil.initBox(feedItemHolder.rlInnerContainer,feedItemHolder.vMainContentBackground,Color.parseColor("#2D2D38"),false,false,false,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);
        feedItemHolder.vMainContentBackground.setVisibility(View.VISIBLE);

        feedItemHolder.vBottomBarBackground = new View(context);
        ViewUtil.initBox(feedItemHolder.rlInnerContainer,feedItemHolder.vBottomBarBackground,Color.parseColor("#212129"),false,false,true,RelativeLayout.LayoutParams.MATCH_PARENT,appState.feedItemBottomBarHeight,0,0,0,0);
        feedItemHolder.vBottomBarBackground.setVisibility(View.VISIBLE);

        feedItemHolder.vRightColorBar = new View(context);
        ViewUtil.initBox(feedItemHolder.rlInnerContainer,feedItemHolder.vRightColorBar,Color.parseColor("#9E0000"),false,true,false,appState.feedItemRightColorBarWidth,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);
        feedItemHolder.vRightColorBar.setVisibility(View.VISIBLE);

        feedItemHolder.ivAvatar = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlInnerContainer,feedItemHolder.ivAvatar,ImageView.ScaleType.CENTER,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(10),0,0);
        feedItemHolder.ivAvatar.setVisibility(View.VISIBLE);

        feedItemHolder.ivVideoThumbnail = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlInnerContainer,feedItemHolder.ivVideoThumbnail,ImageView.ScaleType.CENTER,false,false,false,appState.feedItemThumbnailWidth,appState.feedItemThumbnailHeight,appState.feedItemThumbnailLeftMargin,appState.feedItemThumbnailTopMargin,0,0);
        feedItemHolder.ivVideoThumbnail.setVisibility(View.VISIBLE);

        feedItemHolder.tvUsername = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvUsername,"-",20,true,Color.WHITE,false,false,false,false,appState.feedItemUsernameLeftMargin,appState.feedItemUsernameTopMargin,0,0);
        feedItemHolder.tvUsername.setVisibility(View.VISIBLE);

        feedItemHolder.ivSourceIndicator = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlInnerContainer, feedItemHolder.ivSourceIndicator,ImageView.ScaleType.CENTER,false,true,true,0,0,Util.pixelNumberForDp(15),Util.pixelNumberForDp(5));
        feedItemHolder.ivSourceIndicator.setVisibility(View.VISIBLE);

        feedItemHolder.ivTimeIcon = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlInnerContainer, feedItemHolder.ivTimeIcon,ImageView.ScaleType.CENTER,false,false,true,Util.pixelNumberForDp(10),0,0,Util.pixelNumberForDp(10));
        feedItemHolder.ivTimeIcon.setVisibility(View.VISIBLE);
        feedItemHolder.ivTimeIcon.setImageDrawable(appState.drwblTimeIcon);

        feedItemHolder.tvTimeAgo = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvTimeAgo,"-",15,false,Color.WHITE,false,false,false,true,Util.pixelNumberForDp(46),0,0,Util.pixelNumberForDp(7));
        feedItemHolder.tvTimeAgo.setVisibility(View.VISIBLE);

        feedItemHolder.ivCommentIcon = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlInnerContainer, feedItemHolder.ivCommentIcon,ImageView.ScaleType.CENTER,false,true,false,0,Util.pixelNumberForDp(14),Util.pixelNumberForDp(40),0);
        feedItemHolder.ivCommentIcon.setVisibility(View.VISIBLE);
        feedItemHolder.ivCommentIcon.setImageDrawable(appState.drwblCommentIcon);

        feedItemHolder.tvCommentCount = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvCommentCount,"-",15,false,Color.WHITE,false,false,false,false,appState.screenWidth-Util.pixelNumberForDp(43),Util.pixelNumberForDp(14),0,0);
        feedItemHolder.tvCommentCount.setVisibility(View.VISIBLE);

        feedItemHolder.ivLikeIcon = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlInnerContainer, feedItemHolder.ivLikeIcon,ImageView.ScaleType.CENTER,false,true,false,0,Util.pixelNumberForDp(15),Util.pixelNumberForDp(98),0);
        feedItemHolder.ivLikeIcon.setVisibility(View.VISIBLE);

        feedItemHolder.tvLikeCount = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvLikeCount,"-",15,false,Color.WHITE,false,false,false,false,appState.screenWidth-Util.pixelNumberForDp(102),Util.pixelNumberForDp(15),0,0);
        feedItemHolder.tvLikeCount.setVisibility(View.VISIBLE);

        feedItemHolder.tvTitle = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvTitle,"-",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(170),appState.feedItemThumbnailTopMargin,Util.pixelNumberForDp(10),appState.feedItemBottomBarHeight);
        feedItemHolder.tvTitle.setVisibility(View.VISIBLE);

        feedItemHolder.tvDescription = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvDescription,"",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(170),appState.feedItemThumbnailTopMargin + Util.pixelNumberForDp(20),Util.pixelNumberForDp(10),appState.feedItemBottomBarHeight);
        //feedItemHolder.tvDescription.setVisibility(View.VISIBLE);

        feedItemHolder.tvPost = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlInnerContainer,feedItemHolder.tvPost,"-",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(65),Util.pixelNumberForDp(10),0);
        feedItemHolder.tvPost.setVisibility(View.VISIBLE);

        feedItemHolder.rlMetaItemContainer = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(feedItemHolder.rlInnerContainer,feedItemHolder.rlMetaItemContainer,Color.parseColor("#00000000"),false,false,true,RelativeLayout.LayoutParams.MATCH_PARENT,appState.feedItemThumbnailHeight+appState.feedItemBottomBarHeight+Util.pixelNumberForDp(10),0,0,0,0);

        feedItemHolder.ivPostedImageThumbnail = new ImageView(context);
        ViewUtil.initImageView(feedItemHolder.rlMetaItemContainer, feedItemHolder.ivPostedImageThumbnail,ImageView.ScaleType.CENTER,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(5),0,0);
        feedItemHolder.ivPostedImageThumbnail.setAdjustViewBounds(true);
        feedItemHolder.ivPostedImageThumbnail.setMaxWidth(appState.feedItemThumbnailWidth+1);
        feedItemHolder.ivPostedImageThumbnail.setMaxHeight(appState.feedItemThumbnailHeight+1);

        feedItemHolder.tvMetaTitle = new TextView(context);
        ViewUtil.initTextView(feedItemHolder.rlMetaItemContainer,feedItemHolder.tvMetaTitle,"-",15,true,Color.WHITE,false,false,false,false,appState.feedItemThumbnailWidth+Util.pixelNumberForDp(20),0,appState.feedItemRightColorBarWidth+Util.pixelNumberForDp(5),appState.feedItemBottomBarHeight+Util.pixelNumberForDp(5));

    }

    private void initLoadingAnimationGraphics(LinearLayout llLoadingAnimationContainer)
    {
        llLoadingAnimationContainer.removeAllViews();
        RelativeLayout rlLoadingAnimationContainer = new RelativeLayout(context);
        LinearLayout.LayoutParams innerContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        llLoadingAnimationContainer.addView(rlLoadingAnimationContainer,innerContainerParams);

        ImageView loadingAnimationShape1 = new ImageView(context);
        ImageView loadingAnimationShape2 = new ImageView(context);
        ShapeDrawable circle = new ShapeDrawable( new OvalShape());
        circle.setIntrinsicHeight(60);
        circle.setIntrinsicWidth(60);
        circle.setBounds(new Rect(0, 0, 60, 60));
        circle.getPaint().setColor(Color.parseColor("#37FDFC"));
        loadingAnimationShape1.setImageDrawable(circle);
        loadingAnimationShape2.setImageDrawable(circle);

        loadingAnimationShape1.setVisibility(View.VISIBLE);
        loadingAnimationShape2.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.setMargins(0,20,0,20);
        rlLoadingAnimationContainer.addView(loadingAnimationShape1, params);
        rlLoadingAnimationContainer.addView(loadingAnimationShape2, params);

        AnimationManager.startLoadingAnimation(loadingAnimationShape1,loadingAnimationShape2);
    }

}

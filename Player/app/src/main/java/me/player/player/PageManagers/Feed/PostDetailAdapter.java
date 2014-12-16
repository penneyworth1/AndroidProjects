package me.player.player.PageManagers.Feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.util.Linkify;
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
import me.player.player.Entities.Comment;
import me.player.player.Entities.FeedItem;
import me.player.player.Entities.FeedItemData;
import me.player.player.Entities.FeedItemDataMeta;
import me.player.player.Entities.ImageFromServer;
import me.player.player.Util;
import me.player.player.ViewUtil;

/**
 * Created by stevenstewart on 11/27/14.
 */
public class PostDetailAdapter extends BaseAdapter
{
    Context context;
    AppState appState = AppState.getInstance();

    public PostDetailAdapter(Context contextPar)
    {
        context = contextPar;
    }

    static class PostItemHolder
    {
        View vTopSpacer;
        LinearLayout llOriginalPost;
        View vOriginalPostMainContentBackground;
        RelativeLayout rlOriginalPostInnerContainer;
        View vOriginalPostBottomBarBackground;
        ImageView ivOriginalPostAvatar;
        ImageView ivOriginalPostVideoThumbnail;
        TextView tvOriginalPostTitle;
        ImageView ivOriginalPostSourceIndicator;
        ImageView ivOriginalPostTimeIcon;
        TextView tvOriginalPostTimeAgo;
        ImageView ivOriginalPostCommentIcon;
        TextView tvOriginalPostCommentCount;
        ImageView ivOriginalPostLikeIcon;
        TextView tvOriginalPostLikeCount;
        TextView tvOriginalPostUsername;
        TextView tvOriginalPostPost;
        RelativeLayout rlShowMoreCommentsHolder;
        TextView tvShowMoreComments;

        //Original post metas
        ImageView ivOriginalPostPostedImageThumbnail;
        TextView tvOriginalPostMetaTitle;
        RelativeLayout rlOriginalPostMetaItemContainer;

        LinearLayout llComment;
        RelativeLayout rlCommentInnerContainer;
        TextView tvCommentUsername;
        TextView tvComment;
        ImageView ivCommentAvatar;
        TextView tvCommentTimeAgo;
        ImageView ivCommentLikeIcon;
        TextView tvCommentLikeCount;
        boolean moreCommentsTouchListenerSet = false;

        //Comment metas
        ImageView ivCommentPostedImageThumbnail;
        TextView tvCommentMetaTitle;
        RelativeLayout rlCommentMetaItemContainer;
    }

    @Override
    public int getCount()
    {
        if(appState.selectedFeedItem == null)
            return 0;
        else
            return appState.selectedFeedItem.comments.size() + 1; //One for each comment and one for the original post, like information, and other buttons.
    }

    @Override
    public Object getItem(int position) { return null; }
    @Override
    public long getItemId(int position) { return 0; }

    private void initPostItemHolder(LinearLayout baseLayout, PostItemHolder postItemHolder)
    {
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        AbsListView.LayoutParams baseLayoutParams = new AbsListView.LayoutParams(appState.screenWidth, AbsListView.LayoutParams.WRAP_CONTENT);
        baseLayout.setLayoutParams(baseLayoutParams);

        postItemHolder.llComment = new LinearLayout(context);
        LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(appState.screenWidth,AbsListView.LayoutParams.WRAP_CONTENT);
        postItemHolder.llComment.setLayoutParams(commentParams);
        postItemHolder.llComment.setPadding(0,appState.feedItemMarginTop,0,0);
        baseLayout.addView(postItemHolder.llComment);

        postItemHolder.rlCommentInnerContainer = new RelativeLayout(context);
        LinearLayout.LayoutParams commentInnerContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        postItemHolder.rlCommentInnerContainer.setBackgroundColor(Color.parseColor("#41444E"));
        postItemHolder.llComment.addView(postItemHolder.rlCommentInnerContainer,commentInnerContainerParams);

        postItemHolder.ivCommentAvatar = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlCommentInnerContainer,postItemHolder.ivCommentAvatar,ImageView.ScaleType.CENTER,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(10),0,Util.pixelNumberForDp(10));

        postItemHolder.tvComment = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlCommentInnerContainer,postItemHolder.tvComment,"-TEXT GOES HERE",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(70),Util.pixelNumberForDp(50),Util.pixelNumberForDp(10),Util.pixelNumberForDp(10));

        postItemHolder.tvCommentUsername = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlCommentInnerContainer,postItemHolder.tvCommentUsername,"-",20,true,Color.WHITE,false,false,false,false,Util.pixelNumberForDp(70),Util.pixelNumberForDp(14),0,0);

        postItemHolder.tvCommentTimeAgo = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlCommentInnerContainer,postItemHolder.tvCommentTimeAgo,"-",15,false,Color.WHITE,false,false,false,true,Util.pixelNumberForDp(10),Util.pixelNumberForDp(70),0,0);

        postItemHolder.ivCommentLikeIcon = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlCommentInnerContainer, postItemHolder.ivCommentLikeIcon,ImageView.ScaleType.CENTER,false,true,false,0,Util.pixelNumberForDp(15),Util.pixelNumberForDp(35),0);
        postItemHolder.ivCommentLikeIcon.setImageDrawable(appState.drwblLikeIcon);

        postItemHolder.tvCommentLikeCount = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlCommentInnerContainer,postItemHolder.tvCommentLikeCount,"-",15,false,Color.WHITE,false,false,false,false,appState.screenWidth-Util.pixelNumberForDp(30),Util.pixelNumberForDp(15),0,0);

        //Comment metas
        postItemHolder.rlCommentMetaItemContainer = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(postItemHolder.rlCommentInnerContainer,postItemHolder.rlCommentMetaItemContainer,Color.parseColor("#00000000"),false,false,true,RelativeLayout.LayoutParams.MATCH_PARENT,appState.feedItemThumbnailHeight+Util.pixelNumberForDp(28),0,0,0,0);

        postItemHolder.ivCommentPostedImageThumbnail = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlCommentMetaItemContainer, postItemHolder.ivCommentPostedImageThumbnail,ImageView.ScaleType.CENTER,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(5),0,0);
        postItemHolder.ivCommentPostedImageThumbnail.setAdjustViewBounds(true);
        postItemHolder.ivCommentPostedImageThumbnail.setMaxWidth(appState.feedItemThumbnailWidth+1);
        postItemHolder.ivCommentPostedImageThumbnail.setMaxHeight(appState.feedItemThumbnailHeight+1);

        postItemHolder.tvCommentMetaTitle = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlCommentMetaItemContainer,postItemHolder.tvCommentMetaTitle,"-",15,true,Color.WHITE,false,false,false,false,appState.feedItemThumbnailWidth+Util.pixelNumberForDp(20),0,appState.feedItemRightColorBarWidth+Util.pixelNumberForDp(5),Util.pixelNumberForDp(5));






        postItemHolder.vTopSpacer = new View(context);
        LinearLayout.LayoutParams topSpacerParams = new LinearLayout.LayoutParams(appState.screenWidth,Util.pixelNumberForDp(78));
        postItemHolder.vTopSpacer.setLayoutParams(topSpacerParams);
        baseLayout.addView(postItemHolder.vTopSpacer);

        postItemHolder.llOriginalPost = new LinearLayout(context);
        LinearLayout.LayoutParams originalPostParams = new LinearLayout.LayoutParams(appState.screenWidth,AbsListView.LayoutParams.WRAP_CONTENT);
        postItemHolder.llOriginalPost.setLayoutParams(originalPostParams);
        postItemHolder.llOriginalPost.setPadding(0,appState.feedItemMarginTop,appState.feedItemMarginRight,0);
        baseLayout.addView(postItemHolder.llOriginalPost);

        postItemHolder.rlOriginalPostInnerContainer = new RelativeLayout(context);
        LinearLayout.LayoutParams originalPostInnerContainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        postItemHolder.llOriginalPost.addView(postItemHolder.rlOriginalPostInnerContainer,originalPostInnerContainerParams);

        postItemHolder.vOriginalPostMainContentBackground = new View(context);
        ViewUtil.initBox(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.vOriginalPostMainContentBackground,Color.parseColor("#2D2D38"),false,false,false,RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);

        postItemHolder.vOriginalPostBottomBarBackground = new View(context);
        ViewUtil.initBox(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.vOriginalPostBottomBarBackground,Color.parseColor("#212129"),false,false,true,RelativeLayout.LayoutParams.MATCH_PARENT,appState.feedItemBottomBarHeight,0,0,0,0);

        postItemHolder.ivOriginalPostAvatar = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.ivOriginalPostAvatar,ImageView.ScaleType.CENTER,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(10),0,0);

        postItemHolder.tvOriginalPostUsername = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.tvOriginalPostUsername,"-",20,true,Color.WHITE,false,false,false,false,appState.feedItemUsernameLeftMargin,appState.feedItemUsernameTopMargin,0,0);

        postItemHolder.ivOriginalPostSourceIndicator = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostInnerContainer, postItemHolder.ivOriginalPostSourceIndicator,ImageView.ScaleType.CENTER,false,true,true,0,0,Util.pixelNumberForDp(15),Util.pixelNumberForDp(5));

        postItemHolder.ivOriginalPostTimeIcon = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostInnerContainer, postItemHolder.ivOriginalPostTimeIcon,ImageView.ScaleType.CENTER,false,false,true,Util.pixelNumberForDp(10),0,0,Util.pixelNumberForDp(10));
        postItemHolder.ivOriginalPostTimeIcon.setImageDrawable(appState.drwblTimeIcon);

        postItemHolder.tvOriginalPostTimeAgo = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.tvOriginalPostTimeAgo,"-",15,false,Color.WHITE,false,false,false,true,Util.pixelNumberForDp(46),0,0,Util.pixelNumberForDp(7));

        postItemHolder.ivOriginalPostCommentIcon = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostInnerContainer, postItemHolder.ivOriginalPostCommentIcon,ImageView.ScaleType.CENTER,false,true,false,0,Util.pixelNumberForDp(14),Util.pixelNumberForDp(40),0);
        postItemHolder.ivOriginalPostCommentIcon.setImageDrawable(appState.drwblCommentIcon);

        postItemHolder.tvOriginalPostCommentCount = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.tvOriginalPostCommentCount,"-",15,false,Color.WHITE,false,false,false,false,appState.screenWidth-Util.pixelNumberForDp(37),Util.pixelNumberForDp(14),0,0);

        postItemHolder.ivOriginalPostLikeIcon = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostInnerContainer, postItemHolder.ivOriginalPostLikeIcon,ImageView.ScaleType.CENTER,false,true,false,0,Util.pixelNumberForDp(15),Util.pixelNumberForDp(98),0);

        postItemHolder.tvOriginalPostLikeCount = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.tvOriginalPostLikeCount,"-",15,false,Color.WHITE,false,false,false,false,appState.screenWidth-Util.pixelNumberForDp(96),Util.pixelNumberForDp(15),0,0);

        //Metas
        postItemHolder.rlOriginalPostMetaItemContainer = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.rlOriginalPostMetaItemContainer,Color.parseColor("#00000000"),false,false,true,RelativeLayout.LayoutParams.MATCH_PARENT,appState.feedItemThumbnailHeight+appState.feedItemBottomBarHeight+Util.pixelNumberForDp(10),0,0,0,0);

        postItemHolder.ivOriginalPostPostedImageThumbnail = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostMetaItemContainer, postItemHolder.ivOriginalPostPostedImageThumbnail,ImageView.ScaleType.CENTER,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(5),0,0);
        postItemHolder.ivOriginalPostPostedImageThumbnail.setAdjustViewBounds(true);
        postItemHolder.ivOriginalPostPostedImageThumbnail.setMaxWidth(appState.feedItemThumbnailWidth+1);
        postItemHolder.ivOriginalPostPostedImageThumbnail.setMaxHeight(appState.feedItemThumbnailHeight+1);

        postItemHolder.tvOriginalPostMetaTitle = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostMetaItemContainer,postItemHolder.tvOriginalPostMetaTitle,"-",15,true,Color.WHITE,false,false,false,false,appState.feedItemThumbnailWidth+Util.pixelNumberForDp(20),0,appState.feedItemRightColorBarWidth+Util.pixelNumberForDp(5),appState.feedItemBottomBarHeight+Util.pixelNumberForDp(5));


        //for video items
        postItemHolder.ivOriginalPostVideoThumbnail = new ImageView(context);
        ViewUtil.initImageView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.ivOriginalPostVideoThumbnail,ImageView.ScaleType.CENTER,false,false,false,appState.feedItemThumbnailWidth,appState.feedItemThumbnailHeight,appState.feedItemThumbnailLeftMargin,appState.feedItemThumbnailTopMargin,0,appState.feedItemBottomBarHeight+Util.pixelNumberForDp(5));

        postItemHolder.tvOriginalPostTitle = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.tvOriginalPostTitle,"-",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(170),appState.feedItemThumbnailTopMargin,Util.pixelNumberForDp(10),appState.feedItemBottomBarHeight);

        //for post items
        postItemHolder.tvOriginalPostPost = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlOriginalPostInnerContainer,postItemHolder.tvOriginalPostPost,"-",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(10),Util.pixelNumberForDp(65),Util.pixelNumberForDp(10),0);
        postItemHolder.tvOriginalPostPost.setPadding(0,0,0,appState.feedItemBottomBarHeight+Util.pixelNumberForDp(5));

        postItemHolder.rlShowMoreCommentsHolder = new RelativeLayout(context);
        LinearLayout.LayoutParams moreCommentsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Util.pixelNumberForDp(40));
        moreCommentsParams.setMargins(0,Util.pixelNumberForDp(10),0,0);
        postItemHolder.rlShowMoreCommentsHolder.setBackgroundColor(Color.parseColor("#212129"));
        baseLayout.addView(postItemHolder.rlShowMoreCommentsHolder,moreCommentsParams);

        postItemHolder.tvShowMoreComments = new TextView(context);
        ViewUtil.initTextView(postItemHolder.rlShowMoreCommentsHolder,postItemHolder.tvShowMoreComments,"Show more comments",16,false,Color.WHITE,true,true,false,false,0,0,0,0);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            LinearLayout baseLayout = (LinearLayout) convertView;
            final PostItemHolder postItemHolder;

            if (baseLayout == null || baseLayout.getTag() == null) //The second condition is for when a view is converted from the blank layout from position 0.
            {
                baseLayout = new LinearLayout(context);
                postItemHolder = new PostItemHolder();
                baseLayout.setTag(postItemHolder);
                initPostItemHolder(baseLayout, postItemHolder);
            }
            else
            {
                postItemHolder = (PostItemHolder) baseLayout.getTag();
            }

            if(position == 0) //Position zero is for the original post. This view also contains the top spacer and other ui elements to fetch more comments and show likes etc.
            {
                //Show original post ui elements
                postItemHolder.vTopSpacer.setVisibility(View.VISIBLE);
                postItemHolder.llOriginalPost.setVisibility(View.VISIBLE);
                postItemHolder.vOriginalPostMainContentBackground.setVisibility(View.VISIBLE);
                postItemHolder.rlOriginalPostInnerContainer.setVisibility(View.VISIBLE);
                postItemHolder.vOriginalPostBottomBarBackground.setVisibility(View.VISIBLE);
                postItemHolder.ivOriginalPostAvatar.setVisibility(View.VISIBLE);
                postItemHolder.ivOriginalPostVideoThumbnail.setVisibility(View.VISIBLE);
                postItemHolder.ivOriginalPostSourceIndicator.setVisibility(View.VISIBLE);
                postItemHolder.ivOriginalPostTimeIcon.setVisibility(View.VISIBLE);
                postItemHolder.tvOriginalPostTimeAgo.setVisibility(View.VISIBLE);
                postItemHolder.ivOriginalPostCommentIcon.setVisibility(View.VISIBLE);
                postItemHolder.tvOriginalPostCommentCount.setVisibility(View.VISIBLE);
                postItemHolder.ivOriginalPostLikeIcon.setVisibility(View.VISIBLE);
                postItemHolder.tvOriginalPostLikeCount.setVisibility(View.VISIBLE);
                postItemHolder.tvOriginalPostUsername.setVisibility(View.VISIBLE);

                if (appState.selectedFeedItem.type == Enums.FeedType.VIDEO)
                {
                    postItemHolder.ivOriginalPostVideoThumbnail.setVisibility(View.VISIBLE);
                    postItemHolder.tvOriginalPostTitle.setVisibility(View.VISIBLE);
                    postItemHolder.tvOriginalPostPost.setVisibility(View.GONE);
                }
                else if (appState.selectedFeedItem.type == Enums.FeedType.POST)
                {
                    postItemHolder.tvOriginalPostPost.setVisibility(View.VISIBLE);
                    postItemHolder.ivOriginalPostVideoThumbnail.setVisibility(View.GONE);
                    postItemHolder.tvOriginalPostTitle.setVisibility(View.GONE);
                }

                if(appState.numberOfCommentsShowing < appState.selectedFeedItem.commentCount)
                {
                    postItemHolder.rlShowMoreCommentsHolder.setVisibility(View.VISIBLE);
                    postItemHolder.tvShowMoreComments.setVisibility(View.VISIBLE);
                    if(!postItemHolder.moreCommentsTouchListenerSet)
                    {
                        postItemHolder.tvShowMoreComments.setOnTouchListener(new View.OnTouchListener()
                        {
                            @Override
                            public boolean onTouch(View v, MotionEvent event)
                            {
                                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                                {
                                    appState.mainActivity.showLoadingAnimation();
                                    appState.uiEnabled = false;
                                    appState.numberOfCommentsShowing += 10;
                                    new Thread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            appState.selectedFeedItem.comments = ApiMethods.getComments(Long.toString(appState.selectedFeedItem.id),0,appState.numberOfCommentsShowing);
                                            new Handler(Looper.getMainLooper()).post(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    appState.mainActivity.dismissLoadingAnimation();
                                                    appState.uiEnabled = true;
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    }).start();
                                }
                                return true;
                            }
                        });
                        postItemHolder.moreCommentsTouchListenerSet = true;
                    }
                }
                else
                {
                    postItemHolder.rlShowMoreCommentsHolder.setVisibility(View.GONE);
                    postItemHolder.tvShowMoreComments.setVisibility(View.GONE);
                }

                //Hide comment ui elements
                postItemHolder.llComment.setVisibility(View.GONE);
                postItemHolder.rlCommentInnerContainer.setVisibility(View.GONE);
                postItemHolder.tvComment.setVisibility(View.GONE);
                postItemHolder.ivCommentAvatar.setVisibility(View.GONE);
                postItemHolder.tvCommentUsername.setVisibility(View.GONE);
                postItemHolder.tvCommentTimeAgo.setVisibility(View.GONE);
                postItemHolder.ivCommentLikeIcon.setVisibility(View.GONE);
                postItemHolder.tvCommentLikeCount.setVisibility(View.GONE);

                //Populate views with data from the current selected feed item
                postItemHolder.tvOriginalPostUsername.setText(appState.selectedFeedItem.user.username);
                postItemHolder.tvOriginalPostPost.setText(appState.selectedFeedItem.data.postRaw);
                postItemHolder.tvOriginalPostPost.setAutoLinkMask(Linkify.ALL); //Detect links and make them clickable automatically.
                postItemHolder.tvOriginalPostPost.setLinkTextColor(Color.parseColor("#9999FF"));
                postItemHolder.tvOriginalPostTimeAgo.setText(appState.selectedFeedItem.timeAgo);
                postItemHolder.tvOriginalPostCommentCount.setText(Integer.toString(appState.selectedFeedItem.commentCount));
                postItemHolder.tvOriginalPostLikeCount.setText(Integer.toString(appState.selectedFeedItem.likesCount));
                postItemHolder.tvOriginalPostTitle.setText(appState.selectedFeedItem.data.title);
                if(appState.selectedFeedItem.hasLiked)
                    postItemHolder.ivOriginalPostLikeIcon.setImageDrawable(appState.drwblLikedIcon);
                else
                    postItemHolder.ivOriginalPostLikeIcon.setImageDrawable(appState.drwblLikeIcon);
                postItemHolder.ivOriginalPostLikeIcon.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled && appState.selectedFeedItem.user.id != appState.thisUser.id)
                        {
                            if(appState.selectedFeedItem.hasLiked)
                            {
                                appState.selectedFeedItem.hasLiked = false;
                                appState.selectedFeedItem.likesCount--;
                                postItemHolder.ivOriginalPostLikeIcon.setImageDrawable(appState.drwblLikeIcon);
                            }
                            else
                            {
                                appState.selectedFeedItem.hasLiked = true;
                                appState.selectedFeedItem.likesCount++;
                                postItemHolder.ivOriginalPostLikeIcon.setImageDrawable(appState.drwblLikedIcon);
                            }
                            notifyDataSetChanged();
                            new Thread(new Runnable() { public void run() { ApiMethods.likeFeedItem(appState.selectedFeedItem.hasLiked,Long.toString(appState.selectedFeedItem.id)); } }).start();
                        }
                        return true;
                    }
                });


                if (appState.selectedFeedItem.source == Enums.FeedFilter.PLAYERME)
                {
                    postItemHolder.ivOriginalPostSourceIndicator.setImageDrawable(appState.drwblPlayerFeedItemIndicator);
                }
                else if (appState.selectedFeedItem.source == Enums.FeedFilter.YOUTUBE)
                {
                    postItemHolder.ivOriginalPostSourceIndicator.setImageDrawable(appState.drwblYoutubeFeedItemIndicator);
                }
                else if (appState.selectedFeedItem.source == Enums.FeedFilter.TWITCH)
                {
                    postItemHolder.ivOriginalPostSourceIndicator.setImageDrawable(appState.drwblTwitchFeedItemIndicator);
                }

                //Whenever an image is to be shown on screen, set its "importance" to 1 so that it is now last on the list to be recycled.
                appState.selectedFeedItem.data.avatarImageFromServer.importance = 1;
                appState.selectedFeedItem.data.thumbnailImageFromServer.importance = 1;

                //Avatar image downloading and displaying
                postItemHolder.ivOriginalPostAvatar.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
                if (appState.selectedFeedItem.user.avatarUrl.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(appState.selectedFeedItem.data.avatarImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
                {
                    appState.selectedFeedItem.data.avatarImageFromServer.isAvatar = true; //This lets the download manager know to circle crop the image.
                    appState.selectedFeedItem.data.avatarImageFromServer.mostRecentAdapterIndex = position;
                    ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(appState.selectedFeedItem.data.avatarImageFromServer);
                    if (duplicate != null)
                    {
                        appState.selectedFeedItem.data.avatarImageFromServer = duplicate;
                        if (duplicate.drawable == null)
                            appState.selectedFeedItem.data.avatarImageFromServer.ivsToBePopulated.add(postItemHolder.ivOriginalPostAvatar); //This is the case when a duplicate avatar is seen on screen before the first one gets a chance to donwload.
                    }
                    else //If there is no duplicate, we should always add the imageView to the to-be-animated list.
                    {
                        appState.selectedFeedItem.data.avatarImageFromServer.ivsToBePopulated.add(postItemHolder.ivOriginalPostAvatar);
                    }
                }
                if (appState.selectedFeedItem.data.avatarImageFromServer.drawable != null) //see if the drawable is ready.
                {
                    postItemHolder.ivOriginalPostAvatar.setImageDrawable(appState.selectedFeedItem.data.avatarImageFromServer.drawable);
                    postItemHolder.ivOriginalPostAvatar.setVisibility(View.VISIBLE);
                }

                //Video Thumbnail image downloading and displaying
                postItemHolder.ivOriginalPostVideoThumbnail.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
                if (appState.selectedFeedItem.data.thumbnailUrl.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(appState.selectedFeedItem.data.thumbnailImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
                {
                    appState.selectedFeedItem.data.thumbnailImageFromServer.mostRecentAdapterIndex = position;
                    ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(appState.selectedFeedItem.data.thumbnailImageFromServer);
                    if (duplicate != null)
                    {
                        appState.selectedFeedItem.data.thumbnailImageFromServer = duplicate;
                        if (duplicate.drawable == null)
                            appState.selectedFeedItem.data.thumbnailImageFromServer.ivsToBePopulated.add(postItemHolder.ivOriginalPostVideoThumbnail); //This is the case when a duplicate is seen on screen before the first one gets a chance to donwload.
                    }
                    else //The there is no duplicate, we should always add the imageView to the to-be-animated list.
                    {
                        appState.selectedFeedItem.data.thumbnailImageFromServer.ivsToBePopulated.add(postItemHolder.ivOriginalPostVideoThumbnail);
                    }
                }
                if (appState.selectedFeedItem.data.thumbnailImageFromServer.drawable != null) //The download manager contains this image, see if its drawable is ready.
                {
                    postItemHolder.ivOriginalPostVideoThumbnail.setImageDrawable(appState.selectedFeedItem.data.thumbnailImageFromServer.drawable);
                    postItemHolder.ivOriginalPostVideoThumbnail.setVisibility(View.VISIBLE);
                }
                //Add tap interaction to video link.
                if (appState.selectedFeedItem.type == Enums.FeedType.VIDEO)
                {
                    postItemHolder.ivOriginalPostVideoThumbnail.setOnTouchListener(new View.OnTouchListener()
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
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appState.selectedFeedItem.sourceUrl));
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

                //Posted image thumbnail downloading and displaying
                if(appState.selectedFeedItem.data.hasPostedImageThumbnail)
                {
                    postItemHolder.ivOriginalPostPostedImageThumbnail.setVisibility(View.VISIBLE);
                    final FeedItemDataMeta feedItemDataMeta = appState.selectedFeedItem.data.metas.get(0);
                    postItemHolder.ivOriginalPostPostedImageThumbnail.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
                    if (!(DownloadManager.getInstance().imagesFromServer.contains(feedItemDataMeta.thumbnailImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
                    {
                        feedItemDataMeta.thumbnailImageFromServer.mostRecentAdapterIndex = position;
                        ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(feedItemDataMeta.thumbnailImageFromServer);
                        if (duplicate != null)
                        {
                            feedItemDataMeta.thumbnailImageFromServer = duplicate;
                            if (duplicate.drawable == null)
                                feedItemDataMeta.thumbnailImageFromServer.ivsToBePopulated.add(postItemHolder.ivOriginalPostPostedImageThumbnail); //This is the case when a duplicate is seen on screen before the first one gets a chance to donwload.
                        }
                        else //The there is no duplicate, we should always add the imageView to the to-be-animated list.
                        {
                            feedItemDataMeta.thumbnailImageFromServer.ivsToBePopulated.add(postItemHolder.ivOriginalPostPostedImageThumbnail);
                        }
                    }
                    if (feedItemDataMeta.thumbnailImageFromServer.drawable != null) //The download manager contains this image, see if its drawable is ready.
                    {
                        postItemHolder.ivOriginalPostPostedImageThumbnail.setImageDrawable(feedItemDataMeta.thumbnailImageFromServer.drawable);
                    }
                    postItemHolder.rlOriginalPostMetaItemContainer.setVisibility(View.VISIBLE);
                    if(feedItemDataMeta.title.length()>0 && !feedItemDataMeta.title.equalsIgnoreCase("null"))
                    {
                        postItemHolder.tvOriginalPostMetaTitle.setVisibility(View.VISIBLE);
                        postItemHolder.tvOriginalPostMetaTitle.setAutoLinkMask(Linkify.ALL);
                        postItemHolder.tvOriginalPostMetaTitle.setText(feedItemDataMeta.title);
                    } else { postItemHolder.tvOriginalPostMetaTitle.setText(""); }

                    postItemHolder.tvOriginalPostPost.setPadding(0, 0, 0, appState.feedItemBottomBarHeight + Util.pixelNumberForDp(10) + appState.feedItemThumbnailHeight);
                    //Set tap interaction with meta item thumbnail.
                    postItemHolder.ivOriginalPostPostedImageThumbnail.setOnTouchListener(new View.OnTouchListener()
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
                    postItemHolder.tvOriginalPostPost.setPadding(0, 0, 0, appState.feedItemBottomBarHeight + Util.pixelNumberForDp(5));
                    postItemHolder.ivOriginalPostPostedImageThumbnail.setVisibility(View.GONE);
                    postItemHolder.rlOriginalPostMetaItemContainer.setVisibility(View.GONE);
                    postItemHolder.tvOriginalPostMetaTitle.setVisibility(View.GONE);
                }

            }
            else
            {
                //Hide original post ui elements
                postItemHolder.vTopSpacer.setVisibility(View.GONE);
                postItemHolder.llOriginalPost.setVisibility(View.GONE);
                postItemHolder.vOriginalPostMainContentBackground.setVisibility(View.GONE);
                postItemHolder.rlOriginalPostInnerContainer.setVisibility(View.GONE);
                postItemHolder.vOriginalPostBottomBarBackground.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostAvatar.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostVideoThumbnail.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostSourceIndicator.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostTimeIcon.setVisibility(View.GONE);
                postItemHolder.tvOriginalPostTimeAgo.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostCommentIcon.setVisibility(View.GONE);
                postItemHolder.tvOriginalPostCommentCount.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostLikeIcon.setVisibility(View.GONE);
                postItemHolder.tvOriginalPostLikeCount.setVisibility(View.GONE);
                postItemHolder.tvOriginalPostUsername.setVisibility(View.GONE);
                postItemHolder.tvOriginalPostPost.setVisibility(View.GONE);
                postItemHolder.rlShowMoreCommentsHolder.setVisibility(View.GONE);
                postItemHolder.tvShowMoreComments.setVisibility(View.GONE);
                postItemHolder.ivOriginalPostVideoThumbnail.setVisibility(View.GONE);
                postItemHolder.tvOriginalPostTitle.setVisibility(View.GONE);

                //Show comment ui elements
                postItemHolder.llComment.setVisibility(View.VISIBLE);
                postItemHolder.rlCommentInnerContainer.setVisibility(View.VISIBLE);
                postItemHolder.tvComment.setVisibility(View.VISIBLE);
                postItemHolder.ivCommentAvatar.setVisibility(View.VISIBLE);
                postItemHolder.tvCommentUsername.setVisibility(View.VISIBLE);
                postItemHolder.tvCommentTimeAgo.setVisibility(View.VISIBLE);
                postItemHolder.ivCommentLikeIcon.setVisibility(View.VISIBLE);
                postItemHolder.tvCommentLikeCount.setVisibility(View.VISIBLE);

                //Populate comment ui elements
                final Comment comment = appState.selectedFeedItem.comments.get(position-1);
                postItemHolder.tvComment.setText(comment.data.postRaw);
                postItemHolder.tvComment.setAutoLinkMask(Linkify.ALL); //Detect links and make them clickable automatically.
                postItemHolder.tvComment.setLinkTextColor(Color.parseColor("#9999FF"));
                postItemHolder.tvCommentUsername.setText(comment.user.username);
                postItemHolder.tvCommentTimeAgo.setText(Util.GetTimeagoStringByDate(comment.createdDate,false));
                postItemHolder.tvCommentLikeCount.setText(Integer.toString(comment.likesCount));
                if(comment.hasLiked)
                    postItemHolder.ivCommentLikeIcon.setImageDrawable(appState.drwblLikedIcon);
                else
                    postItemHolder.ivCommentLikeIcon.setImageDrawable(appState.drwblLikeIcon);
                postItemHolder.ivCommentLikeIcon.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled && comment.user.id != appState.thisUser.id)
                        {
                            if(comment.hasLiked)
                            {
                                comment.hasLiked = false;
                                comment.likesCount--;
                                postItemHolder.ivCommentLikeIcon.setImageDrawable(appState.drwblLikeIcon);
                            }
                            else
                            {
                                comment.hasLiked = true;
                                comment.likesCount++;
                                postItemHolder.ivCommentLikeIcon.setImageDrawable(appState.drwblLikedIcon);
                            }
                            notifyDataSetChanged();
                            new Thread(new Runnable() { public void run() { ApiMethods.likeComment(comment.hasLiked,Long.toString(appState.selectedFeedItem.id),Long.toString(comment.id)); } }).start();
                        }
                        return true;
                    }
                });

                //Whenever an image is to be shown on screen, set its "importance" to 1 so that it is now last on the list to be recycled.
                appState.selectedFeedItem.data.avatarImageFromServer.importance = 1;
                appState.selectedFeedItem.data.thumbnailImageFromServer.importance = 1;

                //Avatar image downloading and displaying
                if(comment.data.avatarImageFromServer == null)
                {
                    comment.data.avatarImageFromServer = new ImageFromServer();
                    comment.data.avatarImageFromServer.url = "https:" + comment.user.avatarUrl.replace(".jpg",appState.feedItemAvatarResize + ".jpg");
                }
                postItemHolder.ivCommentAvatar.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
                if(comment.user.avatarUrl.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(comment.data.avatarImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
                {
                    comment.data.avatarImageFromServer.isAvatar = true; //This lets the download manager know to circle crop the image.
                    comment.data.avatarImageFromServer.mostRecentAdapterIndex = position;
                    ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(comment.data.avatarImageFromServer);
                    if (duplicate != null)
                    {
                        comment.data.avatarImageFromServer = duplicate;
                        if (duplicate.drawable == null)
                            comment.data.avatarImageFromServer.ivsToBePopulated.add(postItemHolder.ivCommentAvatar); //This is the case when a duplicate avatar is seen on screen before the first one gets a chance to download.
                    }
                    else //If there is no duplicate, we should always add the imageView to the to-be-animated list.
                    {
                        comment.data.avatarImageFromServer.ivsToBePopulated.add(postItemHolder.ivCommentAvatar);
                    }
                }
                if (comment.data.avatarImageFromServer.drawable != null) //see if the drawable is ready.
                {
                    postItemHolder.ivCommentAvatar.setImageDrawable(comment.data.avatarImageFromServer.drawable);
                    postItemHolder.ivCommentAvatar.setVisibility(View.VISIBLE);
                }

                //Posted image thumbnail downloading and displaying
                if(comment.data.hasPostedImageThumbnail)
                {
                    postItemHolder.ivCommentPostedImageThumbnail.setVisibility(View.VISIBLE);
                    final FeedItemDataMeta feedItemDataMeta = comment.data.metas.get(0);
                    postItemHolder.ivCommentPostedImageThumbnail.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
                    if (!(DownloadManager.getInstance().imagesFromServer.contains(feedItemDataMeta.thumbnailImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
                    {
                        if(feedItemDataMeta.thumbnailImageFromServer == null) //These images don't get processed until now
                        {
                            feedItemDataMeta.thumbnailImageFromServer = new ImageFromServer();
                            feedItemDataMeta.thumbnailImageFromServer.url = "https:" + feedItemDataMeta.thumbnail.replace(".jpg", appState.feedItemThumbnailResize + ".jpg");
                            if(!feedItemDataMeta.url.toLowerCase().contains("http"))
                                feedItemDataMeta.url = "https:" + feedItemDataMeta.url;
                        }

                        feedItemDataMeta.thumbnailImageFromServer.mostRecentAdapterIndex = position;
                        ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(feedItemDataMeta.thumbnailImageFromServer);
                        if (duplicate != null)
                        {
                            feedItemDataMeta.thumbnailImageFromServer = duplicate;
                            if (duplicate.drawable == null)
                                feedItemDataMeta.thumbnailImageFromServer.ivsToBePopulated.add(postItemHolder.ivCommentPostedImageThumbnail); //This is the case when a duplicate is seen on screen before the first one gets a chance to donwload.
                        }
                        else //The there is no duplicate, we should always add the imageView to the to-be-animated list.
                        {
                            feedItemDataMeta.thumbnailImageFromServer.ivsToBePopulated.add(postItemHolder.ivCommentPostedImageThumbnail);
                        }
                    }
                    if (feedItemDataMeta.thumbnailImageFromServer.drawable != null) //The download manager contains this image, see if its drawable is ready.
                    {
                        postItemHolder.ivCommentPostedImageThumbnail.setImageDrawable(feedItemDataMeta.thumbnailImageFromServer.drawable);
                    }
                    postItemHolder.rlCommentMetaItemContainer.setVisibility(View.VISIBLE);
                    if(feedItemDataMeta.title.length()>0 && !feedItemDataMeta.title.equalsIgnoreCase("null"))
                    {
                        postItemHolder.tvCommentMetaTitle.setVisibility(View.VISIBLE);
                        postItemHolder.tvCommentMetaTitle.setAutoLinkMask(Linkify.ALL);
                        postItemHolder.tvCommentMetaTitle.setText(feedItemDataMeta.title);
                    } else { postItemHolder.tvCommentMetaTitle.setText(""); }

                    postItemHolder.tvComment.setPadding(0, 0, 0, appState.feedItemThumbnailHeight + Util.pixelNumberForDp(18));
                    //Set tap interaction with meta item thumbnail.
                    postItemHolder.ivCommentPostedImageThumbnail.setOnTouchListener(new View.OnTouchListener()
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
                    postItemHolder.tvComment.setPadding(0, 0, 0, 0);// appState.feedItemBottomBarHeight + Util.pixelNumberForDp(5));
                    postItemHolder.ivCommentPostedImageThumbnail.setVisibility(View.GONE);
                    postItemHolder.rlCommentMetaItemContainer.setVisibility(View.GONE);
                    postItemHolder.tvCommentMetaTitle.setVisibility(View.GONE);
                }

            }

            return baseLayout;
            //In case we didn't return a view already - this should be impossible so that we can remove the following line.
            //return ViewUtil.getBlankLinearLayoutForListView(context);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return ViewUtil.getBlankLinearLayoutForListView(context);
        }

    }
}

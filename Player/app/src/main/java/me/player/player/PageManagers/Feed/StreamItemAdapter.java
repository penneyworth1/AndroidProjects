package me.player.player.PageManagers.Feed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.player.player.Api.DownloadManager;
import me.player.player.AppState;
import me.player.player.Entities.ImageFromServer;
import me.player.player.Entities.StreamItem;
import me.player.player.Entities.StreamItemData;
import me.player.player.Entities.User;
import me.player.player.Util;
import me.player.player.ViewUtil;

/**
 * Created by stevenstewart on 9/22/14.
 */
public class StreamItemAdapter extends BaseAdapter
{
    Context context;
    AppState appState = AppState.getInstance();
    boolean noItems = false;

    public StreamItemAdapter(Context contextPar)
    {
        context = contextPar;
    }

    static class StreamItemHolder
    {
        View vMainContentBackground;
        View vRightColorBar;
        ImageView ivAvatar;
        ImageView ivVideoThumbnail;
        ImageView ivWatchingIcon;
        TextView tvWatchingCount;
        TextView tvUsername;
        TextView tvTitle;
    }

    @Override
    public int getCount()
    {
        if (appState.fetchedStreamItems.size() == 0)
        {
            noItems = true;
            return 2;
        }
        else
        {
            noItems = false;
            return appState.fetchedStreamItems.size() + 1; //For now, the grid for stream items will not load more at the bottom. Hence +1 rather than +2
        }
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        try
        {
            if(position == 0) //Position zero should return a blank, transparent layout because this will initially be behind the menu and not fully visible.
            {
                RelativeLayout blankRelativeLayout = new RelativeLayout(context);
                AbsListView.LayoutParams blankLayoutParams = new AbsListView.LayoutParams(1, appState.feedGridTopBlankItemHeight);
                blankRelativeLayout.setLayoutParams(blankLayoutParams);
                return blankRelativeLayout;
            }
            if(position == 1 && noItems) //When there are no items, show a message.
            {
                return ViewUtil.getNothingHereListviewItem(context);
            }
//            if (position == (appState.fetchedStreamItems.size()+1)) //Case where we should show the loading animation at the bottom of the screen
//            {
//                RelativeLayout loadingRelativeLayout = new RelativeLayout(context);
//                AbsListView.LayoutParams loadingLayoutParams = new AbsListView.LayoutParams(appState.screenWidth, AbsListView.LayoutParams.WRAP_CONTENT);
//                loadingRelativeLayout.setLayoutParams(loadingLayoutParams);
//                initLoadingAnimationGraphics(loadingRelativeLayout);
//                return loadingRelativeLayout;
//            }

            RelativeLayout relativeLayout = (RelativeLayout) convertView;
            StreamItemHolder streamItemHolder;

            if (relativeLayout == null || relativeLayout.getTag() == null) //The second condition is for when a view is converted from the blank layout from position 0.
            {
                relativeLayout = new RelativeLayout(context);
                streamItemHolder = new StreamItemHolder();
                relativeLayout.setTag(streamItemHolder);
                initStreamItemLayout(relativeLayout, streamItemHolder);
            }
            else
            {
                streamItemHolder = (StreamItemHolder) relativeLayout.getTag();
            }

            final StreamItem streamItem = appState.fetchedStreamItems.get(position-1);
            StreamItemData streamItemData = streamItem.data;
            User user = streamItem.user;

            //Set text elements of this stream item display
            streamItemHolder.tvUsername.setText(user.username);
            streamItemHolder.tvTitle.setText(streamItem.game_name);
            streamItemHolder.tvWatchingCount.setText(streamItem.viewersString); //Long.toString(streamItemData.viewers));

            //Whenever an image is to be shown on screen, set its "importance" to 1 so that it is now last on the list to be recycled.
            streamItem.avatarImageFromServer.importance = 1;
            streamItem.thumbnailImageFromServer.importance = 1;

            //Avatar image downloading and displaying
            streamItemHolder.ivAvatar.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
            if (streamItem.user.avatarUrl.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(streamItem.avatarImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
            {
                streamItem.avatarImageFromServer.isAvatar = true; //This lets the download manager know to circle crop the image.
                streamItem.avatarImageFromServer.mostRecentAdapterIndex = position;
                ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(streamItem.avatarImageFromServer);
                if (duplicate != null)
                {
                    streamItem.avatarImageFromServer = duplicate;
                    if (duplicate.drawable == null)
                        streamItem.avatarImageFromServer.ivsToBePopulated.add(streamItemHolder.ivAvatar); //This is the case when a duplicate avatar is seen on screen before the first one gets a chance to donwload.
                }
                else //If there is no duplicate, we should always add the imageView to the to-be-animated list.
                {
                    streamItem.avatarImageFromServer.ivsToBePopulated.add(streamItemHolder.ivAvatar);
                }
            }
            if (streamItem.avatarImageFromServer.drawable != null) //see if the drawable is ready.
            {
                streamItemHolder.ivAvatar.setImageDrawable(streamItem.avatarImageFromServer.drawable);
                streamItemHolder.ivAvatar.setVisibility(View.VISIBLE);
            }

            //Thumbnail image downloading and displaying
            streamItemHolder.ivVideoThumbnail.setImageDrawable(null); //The recycled imageView may still have an image attached. Clear it.
            if (streamItem.thumbnail.length() > 0 && !(DownloadManager.getInstance().imagesFromServer.contains(streamItem.thumbnailImageFromServer)) && !AppState.getInstance().tooManyItemsqueuedForDownload)
            {
                streamItem.thumbnailImageFromServer.mostRecentAdapterIndex = position;
                ImageFromServer duplicate = DownloadManager.getInstance().addImageToBeDownloaded(streamItem.thumbnailImageFromServer);
                if (duplicate != null)
                {
                    streamItem.thumbnailImageFromServer = duplicate;
                    if (duplicate.drawable == null)
                        streamItem.thumbnailImageFromServer.ivsToBePopulated.add(streamItemHolder.ivVideoThumbnail); //This is the case when a duplicate is seen on screen before the first one gets a chance to donwload.
                }
                else //If there is no duplicate, we should still add the imageView to the to-be-animated list.
                {
                    streamItem.thumbnailImageFromServer.ivsToBePopulated.add(streamItemHolder.ivVideoThumbnail);
                }
            }
            if (streamItem.thumbnailImageFromServer.drawable != null) //The download manager contains this image, see if its drawable is ready.
            {
                streamItemHolder.ivVideoThumbnail.setImageDrawable(streamItem.thumbnailImageFromServer.drawable);
                streamItemHolder.ivVideoThumbnail.setVisibility(View.VISIBLE);
            }

            //Set the touch listener for this thumbnail to open the stream.
            streamItemHolder.ivVideoThumbnail.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                    {
                        try
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(streamItem.url + "/hls"));
                            appState.mainActivity.startActivity(intent);
                        } catch (Exception ex)
                        {
                            Toast.makeText(appState.mainActivity.getApplicationContext(), "Unable to find an app to open this video.", Toast.LENGTH_SHORT).show();
                        }
//                        try
//                        {
//                            Intent intent = new Intent("android.intent.action.MAIN");
//                            intent.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
//                            intent.addCategory("android.intent.category.LAUNCHER");
//                            intent.setData(Uri.parse(streamItem.url + "/hls"));
//                            appState.mainActivity.startActivity(intent);
//                        }
//                        catch (Exception ex)
//                        {
//
//                            Toast.makeText(appState.mainActivity.getApplicationContext(), "You must install the Google Chrome app to do this.", Toast.LENGTH_SHORT).show();
//                        }
                    }
                    return true;
                }
            });



            return relativeLayout;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            //the grid needs some kind of view to be returned
            RelativeLayout blankRelativeLayout = new RelativeLayout(context);
            AbsListView.LayoutParams blankLayoutParams = new AbsListView.LayoutParams(1, appState.feedGridTopBlankItemHeight);
            blankRelativeLayout.setLayoutParams(blankLayoutParams);
            return blankRelativeLayout;
        }
    }

    private void initStreamItemLayout(RelativeLayout baseLayout, StreamItemHolder streamItemHolder)
    {
        baseLayout.setPadding(0,appState.feedItemMarginTop,appState.feedItemMarginRight,0);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(appState.screenWidth, Util.pixelNumberForDp(120));
        baseLayout.setLayoutParams(layoutParams);

        streamItemHolder.vMainContentBackground = new View(context);
        ViewUtil.initBox(baseLayout, streamItemHolder.vMainContentBackground, Color.parseColor("#383C46"), false, false, false, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0, 0, 0);
        streamItemHolder.vMainContentBackground.setVisibility(View.VISIBLE);

        streamItemHolder.vRightColorBar = new View(context);
        ViewUtil.initBox(baseLayout,streamItemHolder.vRightColorBar,Color.parseColor("#2B2E37"),false,true,false,appState.feedItemRightColorBarWidth,RelativeLayout.LayoutParams.MATCH_PARENT,0,0,0,0);
        streamItemHolder.vRightColorBar.setVisibility(View.VISIBLE);

        streamItemHolder.ivAvatar = new ImageView(context);
        ViewUtil.initImageView(baseLayout,streamItemHolder.ivAvatar,ImageView.ScaleType.CENTER,false,false,true,Util.pixelNumberForDp(170),0,0,Util.pixelNumberForDp(10));
        streamItemHolder.ivAvatar.setVisibility(View.VISIBLE);

        streamItemHolder.ivVideoThumbnail = new ImageView(context);
        ViewUtil.initImageView(baseLayout, streamItemHolder.ivVideoThumbnail, ImageView.ScaleType.FIT_XY, false, false, false, appState.feedItemThumbnailWidth, appState.feedItemThumbnailHeight, Util.pixelNumberForDp(10), Util.pixelNumberForDp(10), 0, 0);
        streamItemHolder.ivVideoThumbnail.setVisibility(View.VISIBLE);

        streamItemHolder.tvUsername = new TextView(context);
        ViewUtil.initTextView(baseLayout,streamItemHolder.tvUsername,"-",12,false,Color.WHITE,false,false,false,true,Util.pixelNumberForDp(210),0,0,Util.pixelNumberForDp(18));
        streamItemHolder.tvUsername.setVisibility(View.VISIBLE);

        streamItemHolder.tvTitle = new TextView(context);
        ViewUtil.initTextView(baseLayout,streamItemHolder.tvTitle,"-",15,true,Color.parseColor("#EEEEEE"),false,false,false,false,Util.pixelNumberForDp(170),Util.pixelNumberForDp(10),0,0);
        streamItemHolder.tvTitle.setVisibility(View.VISIBLE);

        streamItemHolder.ivWatchingIcon = new ImageView(context);
        ViewUtil.initImageView(baseLayout, streamItemHolder.ivWatchingIcon,ImageView.ScaleType.CENTER,false,true,true,0,0,Util.pixelNumberForDp(39),Util.pixelNumberForDp(8));
        streamItemHolder.ivWatchingIcon.setVisibility(View.VISIBLE);
        streamItemHolder.ivWatchingIcon.setImageDrawable(appState.drwblEyeIcon);

        streamItemHolder.tvWatchingCount = new TextView(context);
        ViewUtil.initTextView(baseLayout,streamItemHolder.tvWatchingCount,"-",11,false,Color.WHITE,false,false,false,true,appState.screenWidth-appState.feedItemMarginRight-Util.pixelNumberForDp(35),0,0,Util.pixelNumberForDp(9));
        streamItemHolder.tvWatchingCount.setVisibility(View.VISIBLE);

    }

//    private void initLoadingAnimationGraphics(RelativeLayout rlLoadingAnimationContainer)
//    {
//        ImageView loadingAnimationShape1 = new ImageView(context);
//        ImageView loadingAnimationShape2 = new ImageView(context);
//        ShapeDrawable circle = new ShapeDrawable( new OvalShape());
//        circle.setIntrinsicHeight(60);
//        circle.setIntrinsicWidth(60);
//        circle.setBounds(new Rect(0, 0, 60, 60));
//        circle.getPaint().setColor(Color.parseColor("#37FDFC"));
//        loadingAnimationShape1.setImageDrawable(circle);
//        loadingAnimationShape2.setImageDrawable(circle);
//
//        loadingAnimationShape1.setVisibility(View.VISIBLE);
//        loadingAnimationShape2.setVisibility(View.VISIBLE);
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        params.setMargins(0,20,0,20);
//        rlLoadingAnimationContainer.addView(loadingAnimationShape1, params);
//        rlLoadingAnimationContainer.addView(loadingAnimationShape2, params);
//
//        AnimationManager.startLoadingAnimation(loadingAnimationShape1, loadingAnimationShape2);
//    }

}

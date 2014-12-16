package me.player.player.PageManagers.Feed.Listeners;

import android.view.MotionEvent;
import android.view.View;

import me.player.player.BaseTouchListener;
import me.player.player.Constants.Enums.PageType;

/**
 * Created by stevenstewart on 8/24/14.
 */
public class TopBarTouchListener extends BaseTouchListener implements View.OnTouchListener
{
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(appState.uiEnabled)
        {
            //appState.mainActivity.loadNextPage(PageType.LOGIN);


        }

        return false;
    }
}

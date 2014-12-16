package me.player.player.PageManagers.Feed.Listeners;

import android.view.MotionEvent;
import android.view.View;

import me.player.player.BaseTouchListener;

/**
 * Created by stevenstewart on 9/11/14.
 */
public class StreamingButtonTouchListener extends BaseTouchListener implements View.OnTouchListener
{
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (appState.uiEnabled)
        {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP)
            {
                appState.mainActivity.feedPageManager.selectFollowingStreamingSource();
            }
        }
        return true;
    }
}
package me.player.player.PageManagers.Feed.Listeners;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import me.player.player.AppState;

import me.player.player.BaseTouchListener;

/**
 * Created by stevenstewart on 11/20/14.
 */
public class CreatePostButtonTouchListener extends BaseTouchListener implements View.OnTouchListener
{
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        AppState appState = AppState.getInstance();
        appState.mainActivity.feedPageManager.showCreateNewPostWindow(true);

        return false;
    }
}

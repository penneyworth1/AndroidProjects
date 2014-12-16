package me.player.player.PageManagers.Login.Listeners;

import android.view.MotionEvent;
import android.view.View;

import me.player.player.BaseTouchListener;

/**
 * Created by stevenstewart on 8/21/14.
 */

public class BackgroundTouchListener extends BaseTouchListener implements View.OnTouchListener
{
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(appState.uiEnabled)
        {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP)
            {
                appState.mainActivity.dismissSoftKeyboard();
            }
        }
        return true;
    }
}

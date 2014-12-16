package me.player.player.ExtendedAndroidObjects;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by stevenstewart on 9/17/14.
 */
public class GridViewWithOverScroll extends GridView
{
    public GridViewWithOverScroll(Context context)
    {
        super(context);
    }
    private OverScrollListener overScrollListener;

    public void setOverScrollListener(OverScrollListener overScrollListenerPar)
    {
        overScrollListener = overScrollListenerPar;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
    {
        overScrollListener.onOverScroll(scrollX, scrollY);
    }

    public interface OverScrollListener
    {
        void onOverScroll(int scrollX, int scrollY);
    }

}

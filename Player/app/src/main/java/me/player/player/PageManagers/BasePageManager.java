package me.player.player.PageManagers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import me.player.player.Api.ApiMethods;
import me.player.player.AppState;
import me.player.player.Constants.Enums;
import me.player.player.Constants.Enums.AppError;
import me.player.player.MainActivity;

/**
 * Created by stevenstewart on 8/17/14.
 */

public class BasePageManager
{
    //ArrayList<View> viewList = new ArrayList<View>(); //All the views contained in this page.
    protected AppState appState = AppState.getInstance();
    protected Context context;

    public BasePageManager(Context contextPar)
    {
        context = contextPar;
    }

    /**
     * The init method defines the positions of all views and loads them into the layout of the main activity.
     * This should run once when the app first starts. After that, the app should have all possible views loaded in the main layout,
     * although the contents of these views will be flushed (using releaseResourcesAndHideAllViews) when they are not onscreen.
     */
    public AppError init()
    {

        return AppError.NOT_IMPLEMENTED;
    }

    /**
     * The loadResources method is for putting content such as images into the view that are to be displayed in this page.
     * This method is also where any network communication takes place that is needed to present this view such as getting the first few latest posts.
     */
    public AppError loadResources(Activity activity)
    {

        return AppError.NOT_IMPLEMENTED;
    }

    /**
     * The show method is where the animations are added to all the views to have this page transition onto the screen.
     * All views should be set to visible before the animation begins.
     */
    public AppError show()
    {

        return AppError.NOT_IMPLEMENTED;
    }

    /**
     * The show method is where the animations are added to all the views to have this page transition onto the screen.
     * Each view's visibility should be set to GONE at the end of animation, and releaseResourcesAndHideAllViews should be called immediately after that.
     * A thread should be started that sleeps for the same time as the animation, and then clears all animations from each view, sets
     * the views' visibilities to GONE, and then calls releaseResourcesAndHideAllViews.
     */
    public AppError dismiss()
    {

        return AppError.NOT_IMPLEMENTED;
    }

    public AppError releaseResourcesAndHideAllViews()
    {

        return AppError.NOT_IMPLEMENTED;
    }

    protected String getString(int id)
    {
        return context.getString(id);
    }

    protected boolean attemptToRefreshAccessToken()
    {
        ApiMethods.getNewAccessTokenWithRefreshToken();
        if(appState.loginError != Enums.LoginError.NONE)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
            {
                @Override
                public void run()
                {
                    appState.mainActivity.loadNextPage(Enums.PageType.LOGIN);
                }
            });
            return false;
        }
        else
            return true;
    }

}

package me.player.player.PageManagers.Login.Listeners;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import me.player.player.Api.ApiMethods;
import me.player.player.Api.DownloadManager;
import me.player.player.AppState;
import me.player.player.BaseTouchListener;
import me.player.player.Constants.Enums;
import me.player.player.Constants.Enums.PageType;
import me.player.player.Constants.Enums.LoginError;
import me.player.player.PageManagers.Login.LoginPageManager;
import me.player.player.Util;
import me.player.player.Constants.Names;

/**
 * Created by stevenstewart on 9/6/14.
 */
public class LoginButtonTouchListener extends BaseTouchListener implements View.OnTouchListener
{
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(appState.uiEnabled)
        {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP)
            {
                final AppState appState = AppState.getInstance();
                appState.mainActivity.dismissSoftKeyboard();
                appState.mainActivity.showLoadingAnimation();
                appState.mainActivity.loginPageManager.dismissLoginTextFields();

                //TODO - remove this test code
//                if(appState.accessToken.length() > 0)
//                {
//                    appState.mainActivity.loadNextPage(PageType.FEED);
//                    return true;
//                }

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //try { Thread.sleep(1000); } catch (Exception ex) { } //Sleep for a second to let the animations complete.

                        //Attempt login. If successful, an access token and a refresh token will be put into the AppState object.
                        if(appState.refreshToken.length() > 0)
                            ApiMethods.getNewAccessTokenWithRefreshToken();
                        else
                            ApiMethods.getNewAccessTokens(appState.enteredUsername, appState.enteredPassword);

                        if(appState.loginError == LoginError.NONE)
                        {
                            Util.putStringIntoPreferences(Names.PREFS_KEY_USERNAME,appState.enteredUsername,appState.mainActivity);
                            Util.putStringIntoPreferences(Names.PREFS_KEY_ACCESS_TOKEN,appState.accessToken,appState.mainActivity);
                            Util.putStringIntoPreferences(Names.PREFS_KEY_REFRESH_TOKEN,appState.refreshToken,appState.mainActivity);

                            //Fetch User object for this user.
                            appState.thisUser = ApiMethods.getThisUser();
                            //Fetch current user's avatar and make it circular
                            String userAvatarUrl = "https:" + appState.thisUser.avatarUrl.replace(".jpg",appState.currentUserAvatarResize + ".jpg");
                            appState.currentUserCircularAvatar = DownloadManager.downloadImageDirectly(userAvatarUrl,true);


                            new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread callback
                            {
                                @Override
                                public void run()
                                {
                                    appState.backButtonCommands.add(Enums.BackButtonCommand.LOG_OUT);
                                    appState.mainActivity.loadNextPage(PageType.FEED);
                                }
                            });
                        }
                        else
                        {
                            new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread callback
                            {
                                @Override
                                public void run()
                                {
                                    appState.mainActivity.dismissLoadingAnimation();
                                    appState.mainActivity.loginPageManager.showLoginTextFields();
                                }
                            });
                        }



                    }
                }).start();

            }
        }
        return true;
    }
}

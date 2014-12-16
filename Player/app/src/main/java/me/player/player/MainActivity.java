package me.player.player;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import me.player.player.Constants.*;
//import me.player.player.Constants.TimeMeasurements;
import me.player.player.PageManagers.BasePageManager;
import me.player.player.PageManagers.Feed.FeedPageManager;
import me.player.player.PageManagers.Login.LoginPageManager;
import me.player.player.PageManagers.MyProfile.MyProfilePageManager;
import me.player.player.Constants.Enums.*;


public class MainActivity extends Activity
{
    public Activity thisActivity;
    private AppState appState = AppState.getInstance();

    //The layout in which all views reside
    private RelativeLayout globalBaseLayout; //This layout holds the content and the menu
    private RelativeLayout menuLayout;
    private View contentShadowView;
    private RelativeLayout baseContentLayout;

    //Universal UI elements:
    private ImageView loadingAnimationShape1;
    private ImageView loadingAnimationShape2;
    public ImageView menuButton;

    //One of each of the page managers
    public LoginPageManager loginPageManager = new LoginPageManager(this);
    public FeedPageManager feedPageManager = new FeedPageManager(this);
    public MyProfilePageManager myProfilePageManager = new MyProfilePageManager(this);
    private BasePageManager currentPageManager;
    private BasePageManager nextPageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        appState.mainActivity = this; //Reference to the main activity that can be called against from anywhere using appState.
        appState.uiEnabled = true;
        appState.initSharedPreferences();

        globalBaseLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams baseLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        globalBaseLayout.setLayoutParams(baseLayoutParams);

        menuLayout = new RelativeLayout(this);
        menuLayout.setBackgroundColor(Color.parseColor("#232730"));
        RelativeLayout.LayoutParams menuLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        menuLayout.setLayoutParams(menuLayoutParams);
        globalBaseLayout.addView(menuLayout);

        baseContentLayout = new RelativeLayout(this);
        appState.baseContentLayout = baseContentLayout;
        baseContentLayout.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams baseContentLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        baseContentLayout.setLayoutParams(baseContentLayoutParams);
        globalBaseLayout.addView(baseContentLayout);
        setContentView(globalBaseLayout);

        //Set measurements for the app
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        appState.screenWidth = metrics.widthPixels;
        appState.screenHeight = metrics.heightPixels;
        appState.initDerivedMeasurements();

        //Load all views for the app into the main layout.
        loginPageManager.init();
        feedPageManager.init();
        myProfilePageManager.init();

        //Load the loading-please-wait animation on top of the other views.
        initLoadingAnimationGraphics();

        //Load universal UI elements that sit on top
        initMenuButton();

        //Load first page
        loadNextPage(PageType.LOGIN);

        //TODO - remove this test code
        //loadNextPage(PageType.FEED);
    }

    private void initMenuButton()
    {
        menuButton = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(Util.pixelNumberForDp(15),Util.pixelNumberForDp(15),0,0);
        baseContentLayout.addView(menuButton, params);

        appState.drwblMenuButton = Util.getResizedDrawableFromAssets(this, "menu_button.png", Util.pixelNumberForDp(20),true);
        menuButton.setImageDrawable(appState.drwblMenuButton);

        menuButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP && appState.uiEnabled)
                {
                    if(appState.menuOpen)
                    {
                        appState.menuOpen = false;
                        AnimationManager.translateViewQuickly(baseContentLayout,0,0);
                    }
                    else
                    {
                        appState.menuOpen = true;
                        AnimationManager.translateViewQuickly(baseContentLayout,Util.pixelNumberForDp(200),0);
                    }
                }
                return true;
            }
        });

        menuButton.setVisibility(View.GONE);
    }
    public void showMenuButton()
    {
        AnimationManager.addAnimation(menuButton, AnimationType.SHOW_FADE_IN_FROM_LEFT);
    }
    public void dismissMenuButton()
    {
        AnimationManager.addAnimation(menuButton, AnimationType.DISMISS_FADE_OUT_SHRINK);
    }

    private void initLoadingAnimationGraphics()
    {
        loadingAnimationShape1 = new ImageView(this);
        loadingAnimationShape2 = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        baseContentLayout.addView(loadingAnimationShape1, params);
        baseContentLayout.addView(loadingAnimationShape2, params);
        ShapeDrawable circle = new ShapeDrawable( new OvalShape());
        circle.setIntrinsicHeight(100);
        circle.setIntrinsicWidth(100);
        circle.setBounds(new Rect(0, 0, 100, 100));
        circle.getPaint().setColor(Color.parseColor("#37FDFC"));
        loadingAnimationShape1.setImageDrawable(circle);
        loadingAnimationShape2.setImageDrawable(circle);
        loadingAnimationShape1.setVisibility(View.GONE);
        loadingAnimationShape2.setVisibility(View.GONE);
    }

    public void loadNextPage(PageType pageType)
    {
        appState.uiEnabled = false;

        if(pageType == PageType.LOGIN)
            nextPageManager = loginPageManager;
        else if(pageType == PageType.MY_PROFILE)
            nextPageManager = myProfilePageManager;
        else if(pageType == PageType.FEED)
            nextPageManager = feedPageManager;

        showLoadingAnimation();

        new Thread(new Runnable()
        {
            public void run()
            {
                //Prepare all content in the next
                nextPageManager.loadResources(thisActivity);

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (currentPageManager != null)
                        {
                            currentPageManager.dismiss();

                            //Now we need to start a thread that sleeps for the time it takes to visually dismiss the previous page, and afterwards releases its resources on the ui thread (because views may be involved).
                            new Thread(new Runnable()
                            {
                                public void run()
                                {
                                    try { Thread.sleep(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE); } catch (Exception ex) { }
                                    new Handler(Looper.getMainLooper()).post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            currentPageManager.releaseResourcesAndHideAllViews();
                                            currentPageManager = nextPageManager;
                                            appState.uiEnabled = true;
                                        }
                                    });
                                }
                            }).start();
                        }
                        else
                        {
                            currentPageManager = nextPageManager;
                            appState.uiEnabled = true;
                        }

                        nextPageManager.show();
                        dismissLoadingAnimation();
                    }
                });
            }
        }).start();
    }

    public void showLoadingAnimation()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                loadingAnimationShape1.bringToFront();
                loadingAnimationShape2.bringToFront();
                if(!AnimationManager.showingLoadingAnimation)
                {
                    AnimationManager.startLoadingAnimation(loadingAnimationShape1, loadingAnimationShape2);
                }
            }
        });

    }

    public void dismissLoadingAnimation()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() //UI thread - altering the ui
        {
            @Override
            public void run()
            {
                AnimationManager.dismissLoadingAnimation(loadingAnimationShape1, loadingAnimationShape2);
            }
        });
    }

    public void dismissSoftKeyboard()
    {
        try
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText()) // verify if the soft keyboard is open
            {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
    public void popBackButtonCommand(BackButtonCommand backButtonCommandPar) //Try to remove a command of a certain type.
    {
        if(appState.backButtonCommands.size()>0)
        {
            BackButtonCommand backButtonCommand = appState.backButtonCommands.get(appState.backButtonCommands.size() - 1);
            if (backButtonCommand == backButtonCommandPar)
                appState.backButtonCommands.remove(appState.backButtonCommands.size()-1);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) //If there are items in the back button action stack pop one and run it
        {
            if(!appState.uiEnabled)
                return true;

            if(appState.backButtonCommands.size()>0)
            {
                BackButtonCommand backButtonCommand = appState.backButtonCommands.get(appState.backButtonCommands.size()-1);
                if(backButtonCommand == BackButtonCommand.DISMISS_CREATE_POST_DIALOG)
                {
                    feedPageManager.showCreateNewPostWindow(false);
                }
                else if(backButtonCommand == BackButtonCommand.LOG_OUT)
                {
                    appState.mainActivity.loadNextPage(PageType.LOGIN);
                }
                else if(backButtonCommand == BackButtonCommand.DISMISS_POST_DETAIL_DIALOG)
                {
                    feedPageManager.showPostDetailWindow(false);
                }

                appState.backButtonCommands.remove(appState.backButtonCommands.size()-1);
                return true;
            }
            else
            {
                return super.onKeyDown(keyCode, event);
            }
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode)
        {
            case Numbers.ACTIVITY_RESULT_SELECT_PHOTO_FOR_CREATE_POST:
                if(resultCode == RESULT_OK)
                {
                    try
                    {
                        Uri selectedImage = intent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap bmSelectedImage = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bmSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        appState.base64encodedImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    } catch (Exception ex) { ex.printStackTrace(); }
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

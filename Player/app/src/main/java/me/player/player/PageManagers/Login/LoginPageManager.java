package me.player.player.PageManagers.Login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import me.player.player.AnimationManager;
import me.player.player.PageManagers.BasePageManager;
import me.player.player.PageManagers.Login.Listeners.BackgroundTouchListener;
import me.player.player.PageManagers.Login.Listeners.LoginButtonTouchListener;
import me.player.player.PageManagers.Login.Listeners.PasswordFieldInputListener;
import me.player.player.PageManagers.Login.Listeners.UsernameFieldInputListener;
import me.player.player.R;
import me.player.player.Util;
import me.player.player.ViewUtil;
import me.player.player.Constants.Enums.AnimationType;
import me.player.player.Constants.Enums.AppError;

/**
 * Created by stevenstewart on 8/16/14.
 */

public class LoginPageManager extends BasePageManager
{
    RelativeLayout rlGradient;
    TextView tvSignIn;
    TextView tvLogIn;
    ArrayList<ImageView> thumbnailImageViewArray;
    ImageView ivTitle;

    ImageView ivLoginButton;
    ImageView ivUserIcon;
    ImageView ivLockIcon;
    EditText etLogin;
    EditText etPassword;
    View vLoginUnderline;
    View vPasswordUnderline;

    int columns;
    int rows;
    double thumbnailWidth;

    public LoginPageManager(Context contextPar)
    {
        super(contextPar);
    }

    @Override
    public AppError init()
    {
        //Build grid of game thumbnails behind the purple overlay.
        if(appState.screenWidth>1000)
            columns = 5;
        else
            columns = 4;
        thumbnailWidth = appState.screenWidth/columns;
        rows = (int)(appState.screenHeight/thumbnailWidth) +1;
        thumbnailImageViewArray = new ArrayList<ImageView>(columns*rows);
        int linearIndex = 0;
        for(int i=0;i<columns;i++)
            for(int j=0;j<rows;j++)
            {
                ImageView iv = new ImageView(context);
                ViewUtil.initImageView(iv,ImageView.ScaleType.FIT_XY,false,false,false,(int)(i*thumbnailWidth),(int)(j*thumbnailWidth),0,0);
                thumbnailImageViewArray.add(linearIndex, iv);
                linearIndex++;
            }

        //The purple overlay
        rlGradient = new RelativeLayout(context);
        ViewUtil.initLinearGradientRelativeLayout(rlGradient, new int[]{0xEECC80FF, 0xDD4C0080},true, false, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0, 0, 0);
        rlGradient.setOnTouchListener(new BackgroundTouchListener());

        tvSignIn = new TextView(context);
        ViewUtil.initTextView(tvSignIn,getString(R.string.sign_in),30,false,Color.WHITE,true,false,false,false,0,appState.screenHeight/4,0,0);
        ivTitle = new ImageView(context);
        ViewUtil.initImageView(ivTitle,ImageView.ScaleType.FIT_XY,true,false,false,0,Util.pixelNumberForDp(25),0,0);
        ivUserIcon = new ImageView(context);
        ViewUtil.initImageView(ivUserIcon,ImageView.ScaleType.FIT_XY,false,false,false,appState.screenWidth/2-Util.pixelNumberForDp(120),appState.screenHeight/2-Util.pixelNumberForDp(82),0,0);
        ivLockIcon = new ImageView(context);
        ViewUtil.initImageView(ivLockIcon,ImageView.ScaleType.FIT_XY,false,false,false,appState.screenWidth/2-Util.pixelNumberForDp(120),appState.screenHeight/2-Util.pixelNumberForDp(12),0,0);
        etLogin = new EditText(context);
        ViewUtil.initEditText(null,etLogin,20, getString(R.string.username_hint),Color.WHITE,Color.parseColor("#00FFFFFF"),Color.parseColor("#66FFFFFF"),Util.pixelNumberForDp(200),Util.pixelNumberForDp(45),false,false,false,false,appState.screenWidth/2-Util.pixelNumberForDp(90),appState.screenHeight/2-Util.pixelNumberForDp(90),0,0,false);
        etLogin.setText(appState.enteredUsername);
        etLogin.addTextChangedListener(new UsernameFieldInputListener());
        vLoginUnderline = new View(context);
        ViewUtil.initBox(vLoginUnderline,Color.parseColor("#99FFFFFF"),true,false,false,Util.pixelNumberForDp(240),Util.pixelNumberForDp(2),0,appState.screenHeight/2-Util.pixelNumberForDp(50),0,0);
        etPassword = new EditText(context);
        ViewUtil.initEditText(null,etPassword,20,getString(R.string.password_hint),Color.WHITE,Color.parseColor("#00FFFFFF"),Color.parseColor("#66FFFFFF"),Util.pixelNumberForDp(200),Util.pixelNumberForDp(45),false,false,false,false,appState.screenWidth/2-Util.pixelNumberForDp(90),appState.screenHeight/2-Util.pixelNumberForDp(20),0,0,true);
        etPassword.setText(appState.enteredUsername.length()>0 ? "xxxxxxx" : "");
        etPassword.addTextChangedListener(new PasswordFieldInputListener());
        vPasswordUnderline = new View(context);
        ViewUtil.initBox(vPasswordUnderline,Color.parseColor("#99FFFFFF"),true,false,false,Util.pixelNumberForDp(240),Util.pixelNumberForDp(2),0,appState.screenHeight/2+Util.pixelNumberForDp(20),0,0);
        tvLogIn = new TextView(context);
        ViewUtil.initTextView(tvLogIn,getString(R.string.login),25,false,Color.WHITE,true,false,false,false,0,appState.screenHeight/2 + Util.pixelNumberForDp(70),0,0);

        //Login button wire frame graphic
        ivLoginButton = new ImageView(context);
        ViewUtil.initRoundRectWireframeImageView(null,ivLoginButton,true,false,false,Color.WHITE,Util.pixelNumberForDp(245),Util.pixelNumberForDp(55),0,appState.screenHeight/2 + Util.pixelNumberForDp(63),0,0);
        LoginButtonTouchListener loginButtonTouchListener = new LoginButtonTouchListener();
        ivLoginButton.setOnTouchListener(loginButtonTouchListener);

        return AppError.NONE;
    }

    @Override
    public AppError loadResources(Activity activity)
    {
        try { Thread.sleep(1000); } catch (Exception ex) { }

        appState.drwblLoginTitle = Util.getResizedDrawableFromAssets(appState.mainActivity, "login_title.png", appState.loginTitleHeight,true);
        appState.drwblLoginUserIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "user_icon.png", Util.pixelNumberForDp(20),false);
        appState.drwblLoginLockIcon = Util.getResizedDrawableFromAssets(appState.mainActivity, "lock_icon.png", Util.pixelNumberForDp(20),false);



        return AppError.NONE;
    }

    @Override
    public AppError show()
    {
        //Get image resources from assets and insert image data into the image views
        ivTitle.setImageDrawable(appState.drwblLoginTitle);
        ivUserIcon.setImageDrawable(appState.drwblLoginUserIcon);
        ivLockIcon.setImageDrawable(appState.drwblLoginLockIcon);

        //Get game thumbnail images
        int linearIndex = 0;
        for(int i=0;i<columns;i++)
            for(int j=0;j<rows;j++)
            {
                ImageView iv = thumbnailImageViewArray.get(linearIndex);
                ViewUtil.setImageViewWithDrawableFromAssets(iv,"gameThumb" + Integer.toString((linearIndex%72)+1) + ".jpg",(float)thumbnailWidth,false);
                AnimationManager.addAnimation(iv, AnimationType.SHOW_SCALE_IN_RANDOMLY);
                linearIndex++;
            }

        //Animate the rest of the views on to the screen.
        AnimationManager.addAnimation(rlGradient, AnimationType.SHOW_FADE_IN);
        AnimationManager.addAnimation(ivTitle, AnimationType.SHOW_FADE_IN_FROM_LEFT);
        AnimationManager.addAnimation(tvSignIn, AnimationType.SHOW_FADE_IN_RISE);
        showLoginTextFields();
        

        return AppError.NONE;
    }

    @Override
    public AppError dismiss()
    {
        int linearIndex = 0;
        for(int i=0;i<columns;i++)
            for(int j=0;j<rows;j++)
            {
                ImageView iv = thumbnailImageViewArray.get(linearIndex);
                AnimationManager.addAnimation(iv,AnimationType.DISMISS_SCALE_OUT_SPIN_RANDOMLY);
                linearIndex++;
            }

        AnimationManager.addAnimation(rlGradient,AnimationType.DISMISS_FADE_OUT_FAST);
        AnimationManager.addAnimation(tvSignIn,AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(ivTitle,AnimationType.DISMISS_FADE_OUT_SHRINK);
        dismissLoginTextFields();

        return AppError.NONE;
    }

    public void showLoginTextFields()
    {
        tvSignIn.setText(getString(R.string.sign_in));
        AnimationManager.addAnimation(tvLogIn, AnimationType.SHOW_FADE_IN_FROM_LEFT);
        AnimationManager.addAnimation(ivLoginButton, AnimationType.SHOW_FADE_IN_RISE);
        AnimationManager.addAnimation(vLoginUnderline,AnimationType.SHOW_FADE_IN_FROM_RIGHT_LATE);
        AnimationManager.addAnimation(vPasswordUnderline,AnimationType.SHOW_FADE_IN_FROM_RIGHT_LATE);
        AnimationManager.addAnimation(ivUserIcon, AnimationType.SHOW_FADE_IN_FROM_LEFT);
        AnimationManager.addAnimation(ivLockIcon, AnimationType.SHOW_FADE_IN_FROM_LEFT);
        AnimationManager.addAnimation(etPassword, AnimationType.SHOW_FADE_IN);
        AnimationManager.addAnimation(etLogin, AnimationType.SHOW_FADE_IN);
    }

    public void dismissLoginTextFields()
    { //The UI used for logging in will disappear while the login is attempted.
        tvSignIn.setText(getString(R.string.signing_in));
        AnimationManager.addAnimation(tvLogIn, AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(ivLoginButton, AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(vLoginUnderline,AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(vPasswordUnderline,AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(ivUserIcon, AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(ivLockIcon, AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(etPassword, AnimationType.DISMISS_FADE_OUT_SHRINK);
        AnimationManager.addAnimation(etLogin, AnimationType.DISMISS_FADE_OUT_SHRINK);
    }

    @Override
    public AppError releaseResourcesAndHideAllViews()
    {
        int linearIndex = 0;
        for(int i=0;i<columns;i++)
            for(int j=0;j<rows;j++)
            {
                ImageView iv = thumbnailImageViewArray.get(linearIndex);
                iv.setImageDrawable(null);
                iv.setVisibility(View.GONE);
                linearIndex++;
            }

        rlGradient.setVisibility(View.GONE);
        tvSignIn.setVisibility(View.GONE);
        tvLogIn.setVisibility(View.GONE);
        ivTitle.setImageDrawable(null);
        appState.drwblLoginTitle = null;
        ivTitle.setVisibility(View.GONE);
        ivLoginButton.setVisibility(View.GONE);
        ivUserIcon.setImageDrawable(null);
        appState.drwblLoginUserIcon = null;
        ivUserIcon.setVisibility(View.GONE);
        ivLockIcon.setImageDrawable(null);
        appState.drwblLoginLockIcon = null;
        ivLockIcon.setVisibility(View.GONE);
        etLogin.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        vLoginUnderline.setVisibility(View.GONE);
        vPasswordUnderline.setVisibility(View.GONE);













        return AppError.NONE;
    }

}

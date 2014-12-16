package me.player.player;

import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import me.player.player.Constants.Enums.AnimationType;
import me.player.player.Constants.TimeMeasurements;

import java.util.Random;

/**
 * Created by stevenstewart on 8/19/14.
 */

public class AnimationManager
{
    public static boolean showingLoadingAnimation;

    public static void addAnimation(View view, AnimationType animationType)
    {
        view.clearAnimation();
        view.animate().cancel(); //Stop any pending animations.

        if(animationType == AnimationType.SHOW_FADE_IN_RISE)
            addShowFadeInRiseAnimation(view);
        else if(animationType == AnimationType.SHOW_FADE_IN_FROM_LEFT)
            addShowFadeInFromLeftAnimation(view);
        else if(animationType == AnimationType.SHOW_FADE_IN_FROM_RIGHT)
            addShowFadeInFromRightAnimation(view);
        else if(animationType == AnimationType.SHOW_FADE_IN_FROM_RIGHT_LATE)
            addShowFadeInFromRightLateAnimation(view);
        else if(animationType == AnimationType.SHOW_FADE_IN)
            addShowFadeInAnimation(view);
        else if(animationType == AnimationType.SHOW_FADE_IN_QUICKLY)
            addShowFadeInQuicklyAnimation(view);
        else if(animationType == AnimationType.SHOW_FADE_IN_LATE)
            addShowFadeInLateAnimation(view);
        else if(animationType == AnimationType.SHOW_SCALE_IN_RANDOMLY)
            addShowScaleInRandomlyAnimation(view);
        else if(animationType == AnimationType.SHOW_SCALE_IN_QUICKLY_WITH_SPIN)
            addShowScaleInQuicklyWithSpinAnimation(view);
        else if(animationType == AnimationType.SHOW_SCALE_IN_RANDOMLY_VERY_LATE)
            addShowScaleInRandomlyVeryLateAnimation(view);
        else if(animationType == AnimationType.SHOW_ENTER_FROM_TOP)
            addShowEnterFromTopAnimation(view);
        else if(animationType == AnimationType.DISMISS_FADE_OUT_SHRINK)
            addDismissFadeOutShrinkAnimation(view);
        else if(animationType == AnimationType.DISMISS_SCALE_OUT_SPIN_RANDOMLY)
            addDismissScaleOutRandomlySpinAnimation(view);
        else if(animationType == AnimationType.DISMISS_FADE_OUT_FAST)
            addDismissFastFadeOutAnimation(view);
        else if(animationType == AnimationType.DISMISS_EXIT_THROUGH_TOP)
            addDismissExitThroughTopAnimation(view);
        else if(animationType == AnimationType.DISMISS_FADE_OUT_RIGHT)
            addDismissFadeOutRightAnimation(view);

    }
    public static void clearTransformationsAndMakeVisible(View view)
    {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(1f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setRotation(0f);
    }
    private static void addShowEnterFromTopAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setTranslationY(-view.getLayoutParams().height);
        view.animate().translationY(0).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setInterpolator(new DecelerateInterpolator(1.0f));
    }
    private static void addShowFadeInRiseAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.setTranslationY(200);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
        view.animate().translationY(0).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setInterpolator(new DecelerateInterpolator(2.0f));
    }
    private static void addShowFadeInFromLeftAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.setTranslationX(-200);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
        view.animate().translationX(0).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setInterpolator(new DecelerateInterpolator(2.0f));
    }
    private static void addShowFadeInFromRightAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.setTranslationX(200);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
        view.animate().translationX(0).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setInterpolator(new DecelerateInterpolator(1.0f));
    }
    private static void addShowFadeInFromRightLateAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.setTranslationX(500);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
        view.animate().translationX(0).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setInterpolator(new AccelerateInterpolator(1.0f));
    }
    private static void addShowFadeInAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
    }
    private static void addShowFadeInQuicklyAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/4);
    }
    private static void addShowFadeInLateAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setAlpha(0f);
        view.animate().alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setInterpolator(new AccelerateInterpolator(1.5f));
    }
    private static void addShowScaleInRandomlyAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        Random rand = new Random();
        int duration = rand.nextInt(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
        view.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(duration).setInterpolator(new DecelerateInterpolator(1.0f));
    }
    public static void addShowScaleInQuicklyFromLocation(View view, float x, float y, Runnable endAction)
    {
        clearTransformationsAndMakeVisible(view);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setTranslationX(x);
        view.setTranslationY(y);
        //Random rand = new Random();
        //view.setRotation(rand.nextInt(180)-90);
        int duration = TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/4;
        view.animate().translationX(0).translationY(0).scaleX(1f).scaleY(1f).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator());
        if(endAction != null)
            view.animate().withEndAction(endAction);
    }
    public static void addDismissScaleInQuicklyFromLocation(View view, float x, float y, Runnable endAction)
    {
        clearTransformationsAndMakeVisible(view);
        //Random rand = new Random();
        int duration = TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/4;
        view.animate().translationX(x).translationY(y).scaleX(0f).scaleY(0f).setDuration(duration).setInterpolator(new DecelerateInterpolator(1.0f)).withEndAction(endAction);
    }
    private static void addShowScaleInQuicklyWithSpinAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setScaleX(0f);
        view.setScaleY(0f);
        Random rand = new Random();
        view.setRotation(rand.nextInt(180)-90);
        int duration = TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/3;
        view.animate().scaleX(1f).scaleY(1f).rotation(0f).setDuration(duration).setInterpolator(new DecelerateInterpolator(1.0f));
    }
    private static void addShowScaleInRandomlyVeryLateAnimation(View view)
    {
        clearTransformationsAndMakeVisible(view);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        Random rand = new Random();
        int duration = rand.nextInt(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/2);
        view.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(duration).setInterpolator(new DecelerateInterpolator(1.0f)).setStartDelay(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
    }
    private static void addDismissFadeOutShrinkAnimation(View view)
    {
        view.animate().alpha(0f).scaleX(0.5f).scaleY(0.5f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE).setStartDelay(0);
    }
    private static void addDismissFadeOutRightAnimation(View view)
    {
        view.animate().alpha(0f).translationX(200).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
    }
    private static void addDismissScaleOutRandomlySpinAnimation(View view)
    {
        Random rand = new Random();
        int duration = rand.nextInt(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
        int angle = rand.nextInt(90) - 45;
        view.animate().scaleX(0f).scaleY(0f).alpha(0f).rotation(angle).setDuration(duration).setInterpolator(new AccelerateInterpolator(1.5f));
    }
    private static void addDismissFastFadeOutAnimation(View view)
    {
        view.animate().alpha(0f).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/2);
    }
    private static void addDismissExitThroughTopAnimation(View view)
    {
        view.animate().translationY(-view.getLayoutParams().height).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/2);
    }

    public static void translateAndScaleUnderline(View view, int newTranslationX, int newScaleX)
    {
        view.animate().translationX(newTranslationX).scaleX(newScaleX).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(TimeMeasurements.ANIMATION_DURATION_UNDERLINE_MOVE);
    }
    public static void showUnderline(View view, int baseTranslationX, int baseScaleX)
    {
        clearTransformationsAndMakeVisible(view);
        view.setTranslationX(baseTranslationX);
        view.setScaleX(baseScaleX);
        view.setAlpha(0f);
        view.setTranslationY(200);
        view.animate().alpha(1f).translationY(0f).setInterpolator(new DecelerateInterpolator(1.0f)).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE);
    }

    public static void fadeOutPartially(View view, float endAlpha)
    {
        view.animate().alpha(endAlpha).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING/4).setInterpolator(new LinearInterpolator());
    }
    public static void fadeInFromAnyAlphaValue(View view)
    {
        view.animate().alpha(1.0f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING / 2).setInterpolator(new LinearInterpolator());
    }
    public static void translateViewQuickly(final View view, float tx, float ty)
    {
        view.animate().translationX(tx).translationY(ty).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(TimeMeasurements.ANIMATION_DURATION_PAGE_CHANGE/4);
    }
    public static void startLoadingAnimation(final View loadingAnimationShape1, final View loadingAnimationShape2)
    {
        showingLoadingAnimation = true;
        loadingAnimationShape1.setVisibility(View.VISIBLE);
        loadingAnimationShape2.setVisibility(View.VISIBLE);
        loadingAnimationShape1.bringToFront();
        loadingAnimationShape2.bringToFront();

        Runnable startRepeatingLoadingAnimation = new Runnable()
        {
            public void run()
            {
                runLoadingAnimation(loadingAnimationShape1, loadingAnimationShape2); //Repeat until showingLoadingAnimation is false.
            }
        };

        loadingAnimationShape1.setScaleX(0);
        loadingAnimationShape1.setScaleY(0);
        loadingAnimationShape1.setAlpha(0.2f);
        loadingAnimationShape1.animate().scaleX(1f).scaleY(1f).alpha(0.2f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING_GRAPHIC_ENTRANCE_EXIT).withEndAction(startRepeatingLoadingAnimation);
        loadingAnimationShape1.animate().setInterpolator(new DecelerateInterpolator());

        loadingAnimationShape2.setScaleX(0f);
        loadingAnimationShape2.setScaleY(0f);
        loadingAnimationShape2.setAlpha(1f);
        loadingAnimationShape2.animate().scaleX(0.1f).scaleY(0.1f).alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING_GRAPHIC_ENTRANCE_EXIT);
        loadingAnimationShape2.animate().setInterpolator(new DecelerateInterpolator());
    }
    private static void runLoadingAnimation(final View loadingAnimationShape1, final View loadingAnimationShape2)
    {
        Log.d("player","Loading animation repeating...");

        if(showingLoadingAnimation)
        {
            Runnable repeatAnimation = new Runnable()
            {
                public void run()
                {
                    runLoadingAnimation(loadingAnimationShape1, loadingAnimationShape2); //Repeat until showingLoadingAnimation is false.
                }
            };

            loadingAnimationShape1.setScaleX(1);
            loadingAnimationShape1.setScaleY(1);
            loadingAnimationShape1.setAlpha(0.2f);
            loadingAnimationShape1.animate().scaleX(0.1f).scaleY(0.1f).alpha(1f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING).withEndAction(repeatAnimation);
            loadingAnimationShape1.animate().setInterpolator(new AccelerateDecelerateInterpolator());

            loadingAnimationShape2.setScaleX(0.1f);
            loadingAnimationShape2.setScaleY(0.1f);
            loadingAnimationShape2.setAlpha(1f);
            loadingAnimationShape2.animate().scaleX(1f).scaleY(1f).alpha(0.2f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING);
            loadingAnimationShape2.animate().setInterpolator(new AccelerateDecelerateInterpolator());
        }
    }
    public static void dismissLoadingAnimation(final View loadingAnimationShape1,final View loadingAnimationShape2)
    {
        showingLoadingAnimation = false;

        Runnable clearLoadingAnimationGraphics = new Runnable()
        {
            public void run()
            {
                loadingAnimationShape1.setVisibility(View.GONE);
                loadingAnimationShape2.setVisibility(View.GONE);
            }
        };
        loadingAnimationShape1.animate().cancel();
        loadingAnimationShape2.animate().cancel();
        loadingAnimationShape1.animate().scaleX(0f).scaleY(0f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING_GRAPHIC_ENTRANCE_EXIT).withEndAction(clearLoadingAnimationGraphics);
        loadingAnimationShape2.animate().scaleX(0f).scaleY(0f).setDuration(TimeMeasurements.ANIMATION_DURATION_LOADING_GRAPHIC_ENTRANCE_EXIT);
    }

}

package me.player.player.Constants;

/**
 * Created by stevenstewart on 9/11/14.
 */
public class Enums
{
    public enum PageType
    {
        LOGIN, MY_PROFILE, FEED
    }
    public enum AnimationType
    {
        SHOW_FADE_IN_RISE,
        SHOW_FADE_IN_FROM_LEFT,
        SHOW_FADE_IN_FROM_RIGHT,
        SHOW_FADE_IN_FROM_RIGHT_LATE,
        SHOW_FADE_IN,
        SHOW_FADE_IN_QUICKLY,
        SHOW_FADE_IN_LATE,
        SHOW_ENTER_FROM_TOP,
        SHOW_SCALE_IN_RANDOMLY,
        SHOW_SCALE_IN_QUICKLY_WITH_SPIN,
        SHOW_SCALE_IN_RANDOMLY_VERY_LATE,
        DISMISS_SCALE_OUT_SPIN_RANDOMLY,
        DISMISS_FADE_OUT_SHRINK,
        DISMISS_FADE_OUT_FAST,
        DISMISS_EXIT_THROUGH_TOP,
        DISMISS_FADE_OUT_RIGHT
    }
    public enum AppError
    {
        NONE, NOT_IMPLEMENTED;
    }
    public enum LoginError
    {
        NONE, BAD_CREDENTIALS, CONNECTION_ERROR
    }
    public enum FeedType
    {
        VIDEO, POST, STREAM
    }
    public enum FeedFilter
    {
        YOUTUBE, PLAYERME, TWITCH
    }
    public enum FeedSource
    {
        FOLLOWING,DISCOVER,STREAMING,DISCOVER_STREAMING
    }
    public enum StretchMode
    {
        FIT_X_TOP, FIT_X_CENTER, FIT_Y_LEFT, FIT_Y_CENTER
    }
    public enum BackButtonCommand
    {
        LOG_OUT, DISMISS_CREATE_POST_DIALOG, DISMISS_POST_DETAIL_DIALOG
    }
}

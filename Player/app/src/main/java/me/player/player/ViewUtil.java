package me.player.player;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import me.player.player.ExtendedAndroidObjects.GridViewWithOverScroll;

/**
 * Created by stevenstewart on 9/4/14.
 */
public class ViewUtil
{
    public static void initTextView(TextView textView, String text, float fontSize, boolean isBold, int textColor, boolean centerHorizontal, boolean centerVertical, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom)
    { //Implementation that assumes the main activity's base layout
        initTextView(AppState.getInstance().baseContentLayout,textView,text,fontSize, isBold,textColor,centerHorizontal,centerVertical,gravityRight,gravityBottom,marginLeft,marginTop,marginRight,marginBottom);
    }

    public static void initTextView(RelativeLayout baseLayout, TextView textView, String text, float fontSize, boolean isBold, int textColor, boolean centerHorizontal, boolean centerVertical, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        AppState appState = AppState.getInstance();
        textView.setText(text);
        textView.setTypeface(isBold? appState.boldFont : appState.normalFont);
        textView.setTextSize(fontSize);
        textView.setTextColor(textColor);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(centerVertical)
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        textView.setVisibility(View.GONE);
        baseLayout.addView(textView, layoutParams);
    }

    public static void initEditText(RelativeLayout baseLayout, EditText editText, float fontSize, String hint, int textColor, int backgroundColor, int hintColor, int width, int height, boolean centerHorizontal, boolean centerVertical, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom, boolean hideCharacters)
    {
        AppState appState = AppState.getInstance();
        editText.setTypeface(appState.normalFont);
        editText.setTextSize(fontSize);
        editText.setTextColor(textColor);
        editText.setBackgroundColor(backgroundColor);
        //editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setHint(hint);
        editText.setHintTextColor(hintColor);
        editText.setGravity(Gravity.LEFT | Gravity.TOP);
        if(hideCharacters)
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(centerVertical)
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        editText.setVisibility(View.GONE);
        if(baseLayout == null)
            appState.baseContentLayout.addView(editText,layoutParams);
        else
            baseLayout.addView(editText,layoutParams);
    }

    public static void initBox(View view, int color, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        initBox(AppState.getInstance().baseContentLayout, view, color, centerHorizontal, gravityRight, gravityBottom, width, height, marginLeft, marginTop, marginRight, marginBottom);
    }
    public static void initBox(RelativeLayout baseLayout, View view, int color, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        view.setBackgroundColor(color);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        view.setVisibility(View.GONE);
        baseLayout.addView(view, layoutParams);
    }

    public static void initRelativeLayout(RelativeLayout relativeLayout, int color, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        AppState appState = AppState.getInstance();
        relativeLayout.setBackgroundColor(color);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        relativeLayout.setVisibility(View.GONE);
        appState.baseContentLayout.addView(relativeLayout, layoutParams);
    }

    public static void initRelativeLayout(RelativeLayout baseLayout, RelativeLayout relativeLayout, int color, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        relativeLayout.setBackgroundColor(color);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        relativeLayout.setVisibility(View.GONE);
        baseLayout.addView(relativeLayout, layoutParams);
    }

    public static void initGridView(RelativeLayout baseLayout, GridViewWithOverScroll gridView, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        AppState appState = AppState.getInstance();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        gridView.setVisibility(View.GONE);
        gridView.setNumColumns(1);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setSelector(new StateListDrawable()); //Disable the built-in item touch behavior. (lighting up yellow behind the item)
        //gridView.setVerticalSpacing(10);

        if(baseLayout == null)
            appState.baseContentLayout.addView(gridView, layoutParams);
        else
            baseLayout.addView(gridView, layoutParams);
    }

    public static void initLinearGradientRelativeLayout(RelativeLayout rlGradient, int[] colors, boolean topToBottom, boolean centerHorizontal, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        AppState appState = AppState.getInstance();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlGradient.setLayoutParams(layoutParams);
        GradientDrawable gradientDrawable;
        if(topToBottom)
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        else
            gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setCornerRadius(0f);
        rlGradient.setBackground(gradientDrawable);
        rlGradient.setVisibility(View.GONE);
        appState.baseContentLayout.addView(rlGradient);
    }

    public static void initRadialGradientRelativeLayout(RelativeLayout rlGradient, int[] colors, float radius, float centerX, float centerY, boolean centerHorizontal, int width, int height,int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        AppState appState = AppState.getInstance();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,height);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlGradient.setLayoutParams(layoutParams);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradientDrawable.setGradientRadius(radius);
        gradientDrawable.setGradientCenter(centerX, centerY);
        rlGradient.setBackground(gradientDrawable);
        rlGradient.setVisibility(View.GONE);
        appState.baseContentLayout.addView(rlGradient);
    }

    public static void initImageView(ImageView imageView, ImageView.ScaleType scaleType, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        initImageView(AppState.getInstance().baseContentLayout, imageView, scaleType, centerHorizontal, gravityRight, gravityBottom, marginLeft, marginTop, marginRight, marginBottom);
    }
    public static void initImageView(RelativeLayout baseLayout, ImageView imageView, ImageView.ScaleType scaleType, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        initImageView(baseLayout, imageView, scaleType, centerHorizontal, gravityRight, gravityBottom, -1, -1, marginLeft, marginTop, marginRight, marginBottom);
    }
    public static void initImageView(RelativeLayout baseLayout, ImageView imageView, ImageView.ScaleType scaleType, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        RelativeLayout.LayoutParams layoutParams;
        if(width > 0)
            layoutParams = new RelativeLayout.LayoutParams(width, height);
        else
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        imageView.setScaleType(scaleType);
        imageView.setVisibility(View.GONE);
        baseLayout.addView(imageView, layoutParams);
    }

    public static void setImageViewWithDrawableFromAssets(ImageView imageView, String assetFilename, float desiredDimension, boolean dimensionIsHeight)
    {
        AppState appState = AppState.getInstance();

        Drawable drawable = Util.getDrawableFromAssets(assetFilename, appState.mainActivity);
        RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        if(desiredDimension > 0) //Otherwise let the image size determine the size of the view.
        {
            if(dimensionIsHeight)
            { //Here we scale according to a desired height
                float desiredWidth = desiredDimension * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                ivParams.width = (int) desiredWidth;
                ivParams.height = (int) desiredDimension;
            }
            else
            { //Here we scale according to a desired width
                float desiredHeight = desiredDimension * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
                ivParams.width = (int) desiredDimension;
                ivParams.height = (int) desiredHeight;
            }
        }
        else
        {
            ivParams.width = drawable.getIntrinsicWidth();
            ivParams.height = drawable.getIntrinsicHeight();
        }
        imageView.setImageDrawable(drawable);
    }

    public static void initRoundRectWireframeImageView(RelativeLayout baseLayout, ImageView imageView, boolean centerHorizontal, boolean gravityRight, boolean gravityBottom, int strokeColor, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        AppState appState = AppState.getInstance();

        ShapeDrawable loginButtonShape;
        float r1 = Util.pixelNumberForDp(10);
        float r2 = Util.pixelNumberForDp(10);
        loginButtonShape = new ShapeDrawable(new RoundRectShape(new float[] { r1, r2, r1, r2, r1, r2, r1, r2}, null, null));
        loginButtonShape.getPaint().setColor(strokeColor);
        loginButtonShape.getPaint().setStyle(Paint.Style.STROKE);
        loginButtonShape.getPaint().setStrokeWidth(2f);
        loginButtonShape.getPaint().setAntiAlias(true);
        loginButtonShape.setIntrinsicWidth(width-2);
        loginButtonShape.setIntrinsicHeight(height-2);

        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageDrawable(loginButtonShape);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        imageView.setVisibility(View.GONE);
        if(baseLayout == null)
            appState.baseContentLayout.addView(imageView, layoutParams);
        else
            baseLayout.addView(imageView, layoutParams);
    }

    public static void initEditTextWithRoundCorners(RelativeLayout baseLayout, EditText editText, float fontSize, String hint, int textColor, int backgroundColor, int hintColor, int width, int height, boolean centerHorizontal, boolean centerVertical, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom, boolean hideCharacters, float radius, boolean centerTextVertically)
    {
        AppState appState = AppState.getInstance();

        //Background drawable
        ShapeDrawable bgShape;
        //float r1 = Util.pixelNumberForDp(10);
        //float r2 = Util.pixelNumberForDp(10);
        bgShape = new ShapeDrawable(new RoundRectShape(new float[] { radius, radius, radius, radius, radius, radius, radius, radius}, null, null));
        bgShape.getPaint().setColor(backgroundColor);
        bgShape.getPaint().setStyle(Paint.Style.FILL);
        //bgShape.getPaint().setStrokeWidth(2f);
        bgShape.getPaint().setAntiAlias(true);
        bgShape.setIntrinsicWidth(width-2);
        bgShape.setIntrinsicHeight(height-2);

        editText.setTypeface(appState.normalFont);
        editText.setTextSize(fontSize);
        editText.setTextColor(textColor);
        editText.setBackground(bgShape);
        //editText.setBackgroundColor(Color.parseColor("#00000000"));
        if(centerTextVertically)
            editText.setGravity(Gravity.CENTER_VERTICAL);
        else
            editText.setGravity(Gravity.LEFT | Gravity.TOP);
        editText.setHint(hint);
        editText.setHintTextColor(hintColor);
        if(hideCharacters)
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        if(centerHorizontal)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        if(centerVertical)
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        if(gravityRight)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if(gravityBottom)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        editText.setVisibility(View.GONE);
        if(baseLayout == null)
            appState.baseContentLayout.addView(editText,layoutParams);
        else
            baseLayout.addView(editText,layoutParams);
    }

    public static LinearLayout getNothingHereListviewItem(Context context)
    {
        LinearLayout nothingHereLinearLayout = new LinearLayout(context);
        nothingHereLinearLayout.setOrientation(LinearLayout.VERTICAL);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AppState.getInstance().screenWidth, AbsListView.LayoutParams.WRAP_CONTENT);
        nothingHereLinearLayout.setLayoutParams(layoutParams);

        RelativeLayout nothingHereRelativeLayout = new RelativeLayout(context);
        //nothingHereRelativeLayout.setBackgroundColor(Color.parseColor("#383C46"));
        AbsListView.LayoutParams rlLayoutParams = new AbsListView.LayoutParams(AppState.getInstance().screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        nothingHereLinearLayout.setLayoutParams(rlLayoutParams);
        nothingHereLinearLayout.addView(nothingHereRelativeLayout);

        RelativeLayout innerLayout = new RelativeLayout(context);
        ViewUtil.initRelativeLayout(nothingHereRelativeLayout,innerLayout,Color.parseColor("#383C46"),false,false,false,AppState.getInstance().screenWidth,RelativeLayout.LayoutParams.WRAP_CONTENT,0,AppState.getInstance().feedItemMarginTop,0,0);
        innerLayout.setVisibility(View.VISIBLE);

        TextView textView = new TextView(context);
        ViewUtil.initTextView(innerLayout,textView,context.getString(R.string.nothing_here),15,false,Color.WHITE,true,false,false,false,30,30,30,0);
        textView.setVisibility(View.VISIBLE);
        textView.setPadding(0,0,0,30);

        return nothingHereLinearLayout;
    }

    public static LinearLayout getBlankLinearLayoutForListView(Context context)
    {
        AppState appState = AppState.getInstance();

        LinearLayout blankLinearLayout = new LinearLayout(context);
        blankLinearLayout.setOrientation(LinearLayout.VERTICAL);
        AbsListView.LayoutParams blankLayoutParams = new AbsListView.LayoutParams(1, appState.feedGridTopBlankItemHeight);
        blankLinearLayout.setLayoutParams(blankLayoutParams);
        return blankLinearLayout;
    }

}

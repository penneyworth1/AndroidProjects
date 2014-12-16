package stevenstewart.programmingtest.nike;

import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by stevenstewart on 12/15/14.
 */
public class ViewUtil
{
    public static void initTextView(RelativeLayout baseLayout, TextView textView, String text, float fontSize, boolean isBold, int textColor, boolean centerHorizontal, boolean centerVertical, boolean gravityRight, boolean gravityBottom, int marginLeft, int marginTop, int marginRight, int marginBottom)
    {
        textView.setText(text);
        textView.setTypeface(isBold ? Typeface.create("sans-serif", Typeface.BOLD) : Typeface.create("sans-serif-light", Typeface.NORMAL));
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
        baseLayout.addView(textView, layoutParams);
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
        baseLayout.addView(imageView, layoutParams);
    }
}

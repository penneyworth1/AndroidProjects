package me.player.player;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by stevenstewart on 8/24/14.
 */

public class Util
{
    public static long powForNaturalNumbers(long base, long exponent)
    {
        long answer = base;
        for(int i=1;i<exponent;i++)
        {
            answer*=base;
        }
        return answer;
    }
    public static SharedPreferences getSharedPreferences(Context contextPar)
    {
        SharedPreferences prefs = contextPar.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return prefs;
    }
    public static void putStringIntoPreferences(String key, String value, Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).commit();
    }
    public static void putBooleanIntoPreferences(String key, boolean value, Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).commit();
    }
    public static void putIntIntoPreferences(String key, int value, Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        prefs.edit().putInt(key, value).commit();
    }
    public static void putFloatIntoPreferences(String key, float value, Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        prefs.edit().putFloat(key, value).commit();
    }

    public static int pixelNumberForDp(float dp)
    { //This version of this function exists so that it can be called from outside the appState object.
        AppState appState = AppState.getInstance();
        return pixelNumberForDp(dp, appState);
    }
    public static int pixelNumberForDp(float dp, AppState appState)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, appState.mainActivity.getResources().getDisplayMetrics());
    }
    public static int getTextViewWidth(TextView textView)
    {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(),0,textView.getText().length(),bounds);
        return bounds.width();
    }

    public static Drawable getDrawableFromAssets(String filename, Activity activity)
    {
        try
        {
            InputStream inputStream = activity.getAssets().open(filename);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            return drawable;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static SimpleDateFormat getDateFormatter()
    {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        return  formatter;
    }

    public static String GetTimeagoStringByDate(Date date, boolean canShowYesterdayText)
    {
        String returnString = "-";
        Calendar publishedAt = Calendar.getInstance();
        publishedAt.setTime(date);
        Calendar today = Calendar.getInstance();
        double diffInSeconds = (today.getTimeInMillis() - publishedAt.getTimeInMillis())/1000;
        double diffInMinutes = diffInSeconds/60;
        double diffInHours = diffInMinutes/60;
        double diffInDays = diffInHours/24;
        if(diffInDays > 1 && diffInDays < 2 && canShowYesterdayText)  returnString = "yesterday";
        else if(diffInDays>1) returnString = Integer.toString((int)diffInDays) + " d";
        else if(diffInHours>1) returnString = Integer.toString((int)diffInHours) + " h";
        else if(diffInMinutes>1) returnString = Integer.toString((int)diffInMinutes) + " m";
        else if(diffInSeconds>0) returnString = Integer.toString((int)diffInSeconds) + " s";
        else returnString = "now";
        return returnString;
    }

    public static Drawable getResizedDrawableFromAssets(Activity activity, String assetFilename, int desiredDimension, boolean dimensionIsHeight)
    {
        try
        {
            InputStream inputStream = activity.getAssets().open(assetFilename);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            int width, height;
            if(dimensionIsHeight)
            { //Here we scale according to a desired height
                float desiredWidth = desiredDimension * bitmap.getWidth() / bitmap.getHeight();
                width = (int) desiredWidth;
                height = desiredDimension;
            }
            else
            { //Here we scale according to a desired width
                float desiredHeight = desiredDimension * bitmap.getHeight() / bitmap.getWidth();
                width = desiredDimension;
                height = (int) desiredHeight;
            }
            float scaleWidth = ((float) width) / bitmap.getWidth();
            float scaleHeight = ((float) height) / bitmap.getHeight();

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bitmapResized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return new BitmapDrawable(activity.getResources(), bitmapResized);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap getCircleCroppedBitmap(Bitmap sourceBitmap)
    {
        BitmapShader shader;
        shader = new BitmapShader(sourceBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Bitmap targetBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),sourceBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        Paint paint = new Paint(); paint.setAntiAlias(true);
        paint.setShader(shader);

        RectF rect = new RectF(0.0f, 0.0f, sourceBitmap.getWidth(), sourceBitmap.getHeight());

        canvas.drawRoundRect(rect, sourceBitmap.getWidth()/2, sourceBitmap.getWidth()/2, paint);

        return targetBitmap;
    }

}

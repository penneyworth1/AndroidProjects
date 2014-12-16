package stevenstewart.programmingtest.nike;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by stevenstewart on 12/15/14.
 */

public class Util
{
    public static int pixelNumberForDp(float dp, Context context)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

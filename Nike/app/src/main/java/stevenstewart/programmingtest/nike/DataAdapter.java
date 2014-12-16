package stevenstewart.programmingtest.nike;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by stevenstewart on 12/15/14.
 */
public class DataAdapter extends BaseAdapter
{
    //Layout measurements translated to pixels
    private static int DATA_ITEM_PADDING;
    private static int ACCEL_TEXTVIEW_TOP_MARGIN;
    private static int EURO_TEXTVIEW_TOP_MARGIN;
    private static int DELETE_IMAGE_HEIGHT;

    private Drawable drblDeleteIcon;
    private boolean colorsSwitched = false;

    MainActivity context;


    public DataAdapter(MainActivity contextPar)
    {
        super();
        context = contextPar;
        DATA_ITEM_PADDING = Util.pixelNumberForDp(10,context);
        ACCEL_TEXTVIEW_TOP_MARGIN = Util.pixelNumberForDp(25,context);
        EURO_TEXTVIEW_TOP_MARGIN = Util.pixelNumberForDp(50,context);
        DELETE_IMAGE_HEIGHT = Util.pixelNumberForDp(25,context);
        drblDeleteIcon = context.getResources().getDrawable(R.drawable.delete_icon);
    }

    @Override
    public int getCount()
    {
        return context.dataItems.size();
    }

    @Override
    public Object getItem(int position) { return null; }
    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        RelativeLayout baseLayout = (RelativeLayout) convertView;
        final DataItemHolder dataItemHolder;

        if (baseLayout == null || baseLayout.getTag() == null) //The second condition is for when a view is converted from the blank layout from position 0.
        {
            baseLayout = new RelativeLayout(context);
            dataItemHolder = new DataItemHolder();
            baseLayout.setTag(dataItemHolder);
            initDataItemHolder(baseLayout,dataItemHolder);
        }
        else
        {
            dataItemHolder = (DataItemHolder) baseLayout.getTag();
        }
        if(position % 2 == 0)
            baseLayout.setBackgroundColor(Color.parseColor("#444466"));
        else
            baseLayout.setBackgroundColor(Color.parseColor("#00000000"));
        baseLayout.setScaleY(1); //In case an item in this slot was deleted and animated to disappear.

        final DataItem dataItem = context.dataItems.get(position);
        dataItemHolder.tvDate.setText("Shake Date: " + dataItem.date);
        String accelString = Double.toString(dataItem.acceleration);
        if (accelString.length()>5) accelString = accelString.substring(0, 5);
        dataItemHolder.tvAcceleration.setText("Acceleration Magnitude: " + accelString + " m/s^2");
        String euroValueString = Double.toString(dataItem.currentEuroValue);
        if (euroValueString.length()>8) euroValueString = euroValueString.substring(0,8);
        dataItemHolder.tvEuroValue.setText("Value of the Euro: " + euroValueString + " USD");

        final RelativeLayout baseLayoutFinalReference = baseLayout;
        dataItemHolder.ivDelete.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP)
                {
                    Runnable endAction = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            DatabaseHelper databaseHelper = new DatabaseHelper(context);
                            databaseHelper.deleteDataItem(dataItem.id);
                            colorsSwitched = !colorsSwitched;
                            context.updateDataView();
                        }
                    };
                    baseLayoutFinalReference.animate().scaleY(0).setDuration(500).withEndAction(endAction);
                }
                return true;
            }
        });

        return baseLayout;
    }

    static class DataItemHolder
    {
        TextView tvDate;
        TextView tvAcceleration;
        TextView tvEuroValue;
        ImageView ivDelete;
    }
    private void initDataItemHolder(RelativeLayout baseLayout, DataItemHolder dataItemHolder)
    {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);// appState.feedItemHeight);
        baseLayout.setLayoutParams(layoutParams);
        baseLayout.setPadding(DATA_ITEM_PADDING,DATA_ITEM_PADDING,DATA_ITEM_PADDING,DATA_ITEM_PADDING);

        dataItemHolder.tvDate = new TextView(context);
        ViewUtil.initTextView(baseLayout,dataItemHolder.tvDate,"-",13,false, Color.WHITE,false,false,false,false,0,0,0,0);

        dataItemHolder.tvAcceleration = new TextView(context);
        ViewUtil.initTextView(baseLayout,dataItemHolder.tvAcceleration,"-",13,false, Color.WHITE,false,false,false,false,0,ACCEL_TEXTVIEW_TOP_MARGIN,0,0);

        dataItemHolder.tvEuroValue = new TextView(context);
        ViewUtil.initTextView(baseLayout,dataItemHolder.tvEuroValue,"-",13,false, Color.WHITE,false,false,false,false,0,EURO_TEXTVIEW_TOP_MARGIN,0,0);

        dataItemHolder.ivDelete = new ImageView(context);
        ViewUtil.initImageView(baseLayout,dataItemHolder.ivDelete, ImageView.ScaleType.FIT_XY,false,true,false,DELETE_IMAGE_HEIGHT,DELETE_IMAGE_HEIGHT,0,0,0,0);
        dataItemHolder.ivDelete.setImageDrawable(drblDeleteIcon);
    }
}

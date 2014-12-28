package stevenstewart.programmingtest.nike;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Looper;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.provider.ContactsContract.CommonDataKinds;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Calendar;

import stevenstewart.programmingtest.nike.Api.HttpUtil;
import stevenstewart.programmingtest.nike.Api.KeyValuePair;
import stevenstewart.programmingtest.nike.Api.ResponseObject;


public class MainActivity extends ActionBarActivity
{
    static {
        System.loadLibrary("MyLib");
    }

    public native String getNativeString();



    private static int PICK_CONTACT_REQUEST_CODE = 123;
    private static int SENT_EMAIL_REQUEST_CODE = 124;

    public ArrayList<DataItem> dataItems = new ArrayList<>();

    private Activity thisActivity;
    private SoundPool soundPool;
    private int beepIndex;
    private boolean beepedForThisShake = false;
    private boolean waitingForApiResponse = false;
    private Vibrator vibrator;
    private SensorManager sensorManager;
    private final SensorEventListener sensorEventListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            final double a = Math.sqrt((double)(x*x + y*y + z*z)); //Use Pythagorean to get acceleration magnitude.
            if(a > 20)
            {
                Log.d("Acceleration", Double.toString(a));
                if(!beepedForThisShake)
                {
                    soundPool.play(beepIndex, 1.0f, 1.0f, 1, 0, 1);
                    vibrator.vibrate(200);
                    beepedForThisShake = true;

                    if(!waitingForApiResponse) //For now we choose not to stack api requests, and if one request takes time, subsequent shakes will abort the attempt to get data.
                    {
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                waitingForApiResponse = true;
                                ResponseObject responseObject = HttpUtil.makeRequest(HttpUtil.CURRENCY_API_CALL,"GET",null,null,false);
                                if(responseObject.jsonResponse == null) //Try the secondary service
                                    responseObject = HttpUtil.makeRequest(HttpUtil.CURRENCY_API_CALL2,"GET",null,null,false);
                                if(!(responseObject.jsonResponse == null))
                                {
                                    double currentValue = responseObject.jsonResponse.optDouble("Rate",0);
                                    //One of these services returns rate with a lower case r.
                                    if(currentValue < 0.0001) currentValue = responseObject.jsonResponse.optDouble("rate",0);

                                    DataItem dataItem = new DataItem();
                                    dataItem.acceleration = a;
                                    dataItem.currentEuroValue = currentValue;
                                    Calendar calendar = Calendar.getInstance();
                                    dataItem.date = calendar.getTime().toString();

                                    DatabaseHelper databaseHelper = new DatabaseHelper(thisActivity);
                                    long newId = databaseHelper.insertDataItem(dataItem);

                                    updateDataView();
                                }
                                else if(responseObject.responseCode == HttpStatus.SC_SERVICE_UNAVAILABLE)
                                {
                                    showToast("Web service unavailable!");
                                }
                                else
                                {
                                    showToast("Error trying to reach web service!");
                                }
                                waitingForApiResponse = false;
                            }
                        }).start();
                    }
                }
            }
            else { beepedForThisShake = false; } //Don't beep twice if we get two values over the shake threshold in a row.
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };
    private final OnTouchListener shareButtonListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP)
            {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(CommonDataKinds.Email.CONTENT_TYPE); // Show user only contacts w/ Email
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST_CODE);
            }
            return true;
        }
    };

    private String chosenContactName = "";
    private String chosenContactEmail = "";
    private String emailBody = "";
    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
        super.onActivityResult( requestCode, resultCode, intent );
        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_CONTACT_REQUEST_CODE)
            {
                Uri pickedContactData = intent.getData();

                Cursor nameCursor =  getContentResolver().query(pickedContactData, null, null, null, null);
                if (nameCursor.moveToFirst())
                {
                    chosenContactName = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    chosenContactEmail = nameCursor.getString(nameCursor.getColumnIndex(CommonDataKinds.Email.DATA));
                    emailBody = "";
                    for(DataItem dataItem : dataItems)
                    {
                        emailBody+= "Data Item Id: " + Integer.toString(dataItem.id) + "\r\n" +
                        " Date: " + dataItem.date + "\r\n" +
                        " Acceleration: " + Double.toString(dataItem.acceleration) + "\r\n" +
                        " Value of the Euro: " + Double.toString(dataItem.currentEuroValue) + "\r\n" + "-----------------------------------" + "\r\n";
                    }

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",chosenContactEmail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Data from test app");
                    emailIntent.putExtra(Intent.EXTRA_TEXT,emailBody);
                    startActivity(Intent.createChooser(emailIntent, "Email data to " + chosenContactName));
                }
                nameCursor.close();


            }
        }
    }

    //UI Elements
    private ListView listView;
    private DataAdapter dataAdapter;
    private ImageView ivShare;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;

        String test = getNativeString();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        listView = (ListView)findViewById(R.id.mainListView);
        listView.setDivider(null);
        dataAdapter = new DataAdapter(this);
        listView.setAdapter(dataAdapter);
        listView.setSelector(new StateListDrawable()); //Disable the built-in item touch behavior. (lighting up yellow behind the item)
        updateDataView();
        ivShare = (ImageView)findViewById(R.id.ivShare);
        ivShare.setOnTouchListener(shareButtonListener);
    }

    public void updateDataView()
    {
        //Post on the UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                DatabaseHelper databaseHelper = new DatabaseHelper(thisActivity);
                dataItems = databaseHelper.getDataItems();
                dataAdapter.notifyDataSetChanged();
            }
        });
    }
    public void showToast(final String text)
    {
        //Post on the UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(thisActivity,text,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        beepIndex = soundPool.load(this, R.raw.beep, 1);
    }

    @Override
    protected void onPause()
    {
        sensorManager.unregisterListener(sensorEventListener);

        soundPool.release();
        soundPool = null;

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

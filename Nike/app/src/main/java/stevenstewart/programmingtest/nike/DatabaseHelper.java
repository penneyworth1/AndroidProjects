package stevenstewart.programmingtest.nike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by stevenstewart on 12/14/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private Context context;

    public static final String TABLE_ITEMS = "Items";
    public static final String COLUMN_ID = "ItemId";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_ACCELERATION = "Acceleration";
    public static final String COLUMN_EURO_VALUE = "EuroValue";

    private static final String DATABASE_NAME = "TestDB";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ITEMS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_ACCELERATION + " real not null, "
            + COLUMN_EURO_VALUE + " real not null"
            + ");";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context contextPar)
    {
        super(contextPar, DATABASE_NAME, null, DATABASE_VERSION);
        context = contextPar;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public long insertDataItem(DataItem dataItem)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCELERATION, dataItem.acceleration);
        values.put(COLUMN_DATE, dataItem.date);
        values.put(COLUMN_EURO_VALUE, dataItem.currentEuroValue);
        long insertId = db.insert(TABLE_ITEMS, null, values);

        db.close();
        return insertId;
    }

    public void deleteDataItem(long itemId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = " + Long.toString(itemId), null);
        db.close();
    }

    public ArrayList<DataItem> getDataItems()
    {
        ArrayList<DataItem> dataItemList = new ArrayList<DataItem>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT " + COLUMN_ID + "," + COLUMN_ACCELERATION + "," + COLUMN_DATE + "," + COLUMN_EURO_VALUE + " FROM " + TABLE_ITEMS, null);
        if(c.moveToFirst())
        {
            do
            {
                DataItem dataItem = new DataItem();
                dataItem.id = c.getInt(0);
                dataItem.acceleration = c.getDouble(1);
                dataItem.date = c.getString(2);
                dataItem.currentEuroValue = c.getDouble(3);
                dataItemList.add(dataItem);
            }
            while(c.moveToNext());
        }

        c.close();
        db.close();
        return dataItemList;
    }

}

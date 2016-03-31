package galvanique.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.GeneralDAO;
import galvanique.db.dao.GsrDAO;
import galvanique.db.dao.MoodLogDAO;

/**
 * This is the basic component to create and manage the database.
 * This class should only be used in the {@link GeneralDAO}.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventreports";
    private static final String TAG = "MyDBHelper";

    public MyDBHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MyDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(TAG, "database initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO prepopulate any values we want already in the tables, set up remaining tables
        // Mood
        db.execSQL(MoodLogDAO.TABLE_CREATE);
        Log.d(TAG, "table " + MoodLogDAO.TABLE_NAME + " was created");
        // GSR
        db.execSQL(GsrDAO.TABLE_CREATE);
        Log.d(TAG, "table " + GsrDAO.TABLE_NAME + " was created");
        // Coping strategies
        db.execSQL(CopingStrategyLogDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyLogDAO.TABLE_NAME + " was created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Utils.d(this, "===========DROPPING DBs====== old: "+oldVersion+"====new: "+newVersion+"=========");

        // clear old schema and data
        //db.execSQL("DROP TABLE IF EXISTS " + ReportDAO.TABLE_NAME);

        // create new schema
        //onCreate(db);

    }

}
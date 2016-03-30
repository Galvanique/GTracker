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
        String ROW1 = "INSERT INTO " + CopingStrategyLogDAO.TABLE_NAME + " ("
                + CopingStrategyLogDAO.CNAME_ID + ", " + CopingStrategyLogDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyLogDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyLogDAO.CNAME_LONGTERM + ", "
                + CopingStrategyLogDAO.CNAME_EFFECTIVENESS + ") Values ('0', '11111', 'testLongOne', '1', '9')";
        db.execSQL(ROW1);
        String ROW2 = "INSERT INTO " + CopingStrategyLogDAO.TABLE_NAME + " ("
                + CopingStrategyLogDAO.CNAME_ID + ", " + CopingStrategyLogDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyLogDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyLogDAO.CNAME_LONGTERM + ", "
                + CopingStrategyLogDAO.CNAME_EFFECTIVENESS + ") Values ('1', '22222', 'testLongTwo', '1', '5')";
        db.execSQL(ROW2);
        String ROW3 = "INSERT INTO " + CopingStrategyLogDAO.TABLE_NAME + " ("
                + CopingStrategyLogDAO.CNAME_ID + ", " + CopingStrategyLogDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyLogDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyLogDAO.CNAME_LONGTERM + ", "
                + CopingStrategyLogDAO.CNAME_EFFECTIVENESS + ") Values ('2', '33333', 'testShortOne', '0', '10')";
        db.execSQL(ROW3);
        String ROW4 = "INSERT INTO " + CopingStrategyLogDAO.TABLE_NAME + " ("
                + CopingStrategyLogDAO.CNAME_ID + ", " + CopingStrategyLogDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyLogDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyLogDAO.CNAME_LONGTERM + ", "
                + CopingStrategyLogDAO.CNAME_EFFECTIVENESS + ") Values ('3', '44444', 'testShortTwo', '0', '3')";
        db.execSQL(ROW4);
        Log.d(TAG, "table " + CopingStrategyLogDAO.TABLE_NAME + " was initialized");
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
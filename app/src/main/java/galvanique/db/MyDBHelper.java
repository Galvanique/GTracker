package galvanique.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import galvanique.db.MoodDAO;

/**
 * This is the basic component to create and manage the database. 
 * This class should only be used in the {@link GeneralDAO}.
 *
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
        Log.d(TAG,"database initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoodDAO.TABLE_CREATE);
        Log.d(TAG, "table " + MoodDAO.TABLE_NAME + " was created");
        db.execSQL(CopingStrategyDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyDAO.TABLE_NAME + " was created");
        //db.execSQL(CopingStrategyDAO.PRELOAD_STRATEGIES);
        String ROW1 = "INSERT INTO " + CopingStrategyDAO.TABLE_NAME + " ("
                + CopingStrategyDAO.CNAME_ID + ", " + CopingStrategyDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyDAO.CNAME_LONGTERM + ", "
                + CopingStrategyDAO.CNAME_EFFECTIVENESS + ") Values ('0', '11111', 'testLongOne', '1', '9')";
        db.execSQL(ROW1);
        String ROW2 = "INSERT INTO " + CopingStrategyDAO.TABLE_NAME + " ("
                + CopingStrategyDAO.CNAME_ID + ", " + CopingStrategyDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyDAO.CNAME_LONGTERM + ", "
                + CopingStrategyDAO.CNAME_EFFECTIVENESS + ") Values ('1', '22222', 'testLongTwo', '1', '5')";
        db.execSQL(ROW2);
        String ROW3 = "INSERT INTO " + CopingStrategyDAO.TABLE_NAME + " ("
                + CopingStrategyDAO.CNAME_ID + ", " + CopingStrategyDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyDAO.CNAME_LONGTERM + ", "
                + CopingStrategyDAO.CNAME_EFFECTIVENESS + ") Values ('2', '33333', 'testShortOne', '0', '10')";
        db.execSQL(ROW3);
        String ROW4 = "INSERT INTO " + CopingStrategyDAO.TABLE_NAME + " ("
                + CopingStrategyDAO.CNAME_ID + ", " + CopingStrategyDAO.CNAME_TIMESTAMP + ", "
                + CopingStrategyDAO.CNAME_COPINGSTRATEGY + ", " + CopingStrategyDAO.CNAME_LONGTERM + ", "
                + CopingStrategyDAO.CNAME_EFFECTIVENESS + ") Values ('3', '44444', 'testShortTwo', '0', '3')";
        db.execSQL(ROW4);
        Log.d(TAG, "table " + CopingStrategyDAO.TABLE_NAME + " was initialized");
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
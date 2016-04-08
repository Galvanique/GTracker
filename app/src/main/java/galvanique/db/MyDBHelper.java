package galvanique.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import galvanique.db.dao.BehaviorDAO;
import galvanique.db.dao.BeliefDAO;
import galvanique.db.dao.CopingStrategyDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.CopingStrategyLogDefaultDAO;
import galvanique.db.dao.GeneralDAO;
import galvanique.db.dao.GsrDAO;
import galvanique.db.dao.MoodDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.dao.TriggerDAO;
import galvanique.db.entities.MoodLog;

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
        // TODO-someone prepopulate any values we want already in the static cslogdefault and mood tables
        // Behavior
        db.execSQL(BehaviorDAO.TABLE_CREATE);
        Log.d(TAG, "table " + BehaviorDAO.TABLE_NAME + " was created");
        // Belief
        db.execSQL(BeliefDAO.TABLE_CREATE);
        Log.d(TAG, "table " + BeliefDAO.TABLE_NAME + " was created");
        // CopingStrategy
        db.execSQL(CopingStrategyDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyDAO.TABLE_NAME + " was created");
        // CopingStrategyLog
        db.execSQL(CopingStrategyLogDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyLogDAO.TABLE_NAME + " was created");
        // CopingStrategyLogDefault
        db.execSQL(CopingStrategyLogDefaultDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyLogDefaultDAO.TABLE_NAME + " was created");
        // Gsr
        db.execSQL(GsrDAO.TABLE_CREATE);
        Log.d(TAG, "table " + GsrDAO.TABLE_NAME + " was created");
        // Mood
        db.execSQL(MoodDAO.TABLE_CREATE);
        Log.d(TAG, "table " + MoodDAO.TABLE_NAME + " was created");
        // Prepopulate static mood table
        // TODO-tyler this is really bad practice sorry
        MoodLog.Mood moods[] = MoodLog.Mood.values();
        String moodStrings[] =  new String[moods.length];
        for (int i = 0; i < moods.length; i++) {
            moodStrings[i] = moods[i].name();
        }
        for (String mood : moodStrings) {
            String ROW = "INSERT INTO " + MoodDAO.TABLE_NAME + " ("
                    + MoodDAO.CNAME_NAME + ") Values ('" + mood + "')";
            db.execSQL(ROW);
        }
        // MoodLog
        db.execSQL(MoodLogDAO.TABLE_CREATE);
        Log.d(TAG, "table " + MoodLogDAO.TABLE_NAME + " was created");
        // Trigger
        db.execSQL(TriggerDAO.TABLE_CREATE);
        Log.d(TAG, "table " + TriggerDAO.TABLE_NAME + " was created");
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
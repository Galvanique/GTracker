package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import galvanique.db.entities.Behavior;

public class BehaviorDAO extends GeneralDAO {
    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------
    public static String TABLE_NAME = "Behavior";

    public static final String TAG = "BehaviorDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_NAME = "name";
    public static final String CNAME_STRING = "string"; 

    public static final String[] PROJECTION = {
    	CNAME_ID,
    	CNAME_NAME,
        CNAME_STRING
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_NAME = 1;
    public final static int CNUM_STRING = 2;


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
        CNAME_ID + " INTEGER PRIMARY KEY, " +
        CNAME_NAME + " TEXT" +
        CNAME_STRING + " TEXT" +
        ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_STRING = CNAME_STRING + "=?";


    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public BehaviorDAO(Context context) {
    	super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public Behavior getBehaviorById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return cursor2behavior(c);
    }
    public Behavior getBehaviorByString(String s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_STRING,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2behavior_2(c);
    }


    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static Behavior cursor2behavior(Cursor c) {
        c.moveToFirst();
        Behavior r = new Behavior();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }
    private static Behavior cursor2behavior_2(Cursor c) {
        c.moveToFirst();
        Behavior r = new Behavior();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        r.string = c.getString(CNUM_STRING);
        return r;
    }

}

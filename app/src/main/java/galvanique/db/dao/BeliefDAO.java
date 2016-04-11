package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import galvanique.db.entities.Belief;

public class BeliefDAO extends GeneralDAO {
    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------
    public static String TABLE_NAME = "Belief";

    public static final String TAG = "BeliefDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_NAME = "name";
    public static final String CNAME_STRING = "string"; //need to check real name

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

    public BeliefDAO(Context context) {
    	super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public Belief getBeliefById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return cursor2belief(c);
    }

    public Belief getBeliefByString(String s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_STRING,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2belief_2(c);
    }

    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static Belief cursor2belief(Cursor c) {
        c.moveToFirst();
        Belief r = new Belief();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }
    private static Belief cursor2belief_2(Cursor c) {
        c.moveToFirst();
        Belief r = new Belief();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        r.string = c.getString(CNAME_STRING);
        return r;
    }

}

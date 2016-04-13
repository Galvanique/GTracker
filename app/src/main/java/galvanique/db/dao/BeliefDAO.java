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

    public static final String[] PROJECTION = {
    	CNAME_ID,
    	CNAME_NAME
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_NAME = 1;

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
        CNAME_ID + " INTEGER PRIMARY KEY, " +
        CNAME_NAME + " TEXT" +
        ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_NAME = CNAME_NAME + "=?";


    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public BeliefDAO(Context context) {
    	super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    // Returns id of inserted row
    public long insert(Belief r) {
        ContentValues cv = belief2ContentValues(r);
        long rowid = db.insert(TABLE_NAME, null, cv);
        return rowid;
    }

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

    public Belief[] getBeliefByString(String s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_NAME,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2beliefs(c);
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
        return r;
    }

    public static Belief[] cursor2beliefs(Cursor c) {
        c.moveToFirst();
        LinkedList<Belief> beliefs = new LinkedList<Belief>();
        while (!c.isAfterLast()) {
            Belief r = new Belief();
            r.id = c.getInt(CNUM_ID);
            r.name = c.getString(CNUM_NAME);
            beliefs.add(r);
            c.moveToNext();
        }
        return beliefs.toArray(new Belief[0]);
    }

    private static ContentValues belief2ContentValues(Belief r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_NAME, r.name);
        return cv;
    }

}

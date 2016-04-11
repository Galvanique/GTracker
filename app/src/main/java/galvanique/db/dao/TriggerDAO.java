package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import galvanique.db.entities.Trigger;

public class TriggerDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------
    public static String TABLE_NAME = "Trigger";

    public static final String TAG = "TriggerDAO";

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
        ");//";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public TriggerDAO(Context context) {
    	super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public Trigger getTriggerById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return cursor2trigger(c);
    }

//  need to be done
    public Trigger getTriggerByString(string s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2trigger(c);
    }
//  need to be done

    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static Trigger cursor2trigger(Cursor c) {
        c.moveToFirst();
        Trigger r = new Trigger();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }

}

package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import galvanique.db.entities.CopingStrategy;

public class CopingStrategyDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------

    public static String TABLE_NAME = "copingStrategyLog";

    public static final String TAG = "CopingStrategyDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_NAME = "name";

    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_NAME
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_NAME= 1;

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY, " +
            CNAME_NAME + " TEXT" +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public CopingStrategyDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public CopingStrategy getCopingStrategyById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return cursor2copingStrategy(c);
    }

    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static CopingStrategy cursor2copingStrategy(Cursor c) {
        c.moveToFirst();
        CopingStrategy r = new CopingStrategy();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }
}

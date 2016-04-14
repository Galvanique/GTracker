package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_NAME = CNAME_NAME + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public TriggerDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    // Returns id of inserted row
    public long insert(Trigger r) {
        ContentValues cv = trigger2ContentValues(r);
        long rowid = db.insert(TABLE_NAME, null, cv);
        return rowid;
    }

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

    public Trigger[] getTriggerByString(String s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_NAME,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2triggers(c);
    }

    // --------------------------------------------
    // TRIGGER-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static Trigger cursor2trigger(Cursor c) {
        c.moveToFirst();
        Trigger r = new Trigger();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }

    private static Trigger cursor2trigger_2(Cursor c) {
        c.moveToFirst();
        Trigger r = new Trigger();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }

    public static Trigger[] cursor2triggers(Cursor c) {
        c.moveToFirst();
        LinkedList<Trigger> triggers = new LinkedList<Trigger>();
        while (!c.isAfterLast()) {
            Trigger r = new Trigger();
            r.id = c.getInt(CNUM_ID);
            r.name = c.getString(CNUM_NAME);
            triggers.add(r);
            c.moveToNext();
        }
        return triggers.toArray(new Trigger[0]);
    }

    private static ContentValues trigger2ContentValues(Trigger r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_NAME, r.name);
        return cv;
    }

}

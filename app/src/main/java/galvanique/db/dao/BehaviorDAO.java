package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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

    public BehaviorDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    // Returns id of inserted row
    public long insert(Behavior r) {
        ContentValues cv = behavior2ContentValues(r);
        long rowid = db.insert(TABLE_NAME, null, cv);
        return rowid;
    }

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

    public Behavior[] getBehaviorByString(String s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_NAME,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2behaviors(c);
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
        return r;
    }

    public static Behavior[] cursor2behaviors(Cursor c) {
        c.moveToFirst();
        LinkedList<Behavior> behaviors = new LinkedList<Behavior>();
        while (!c.isAfterLast()) {
            Behavior r = new Behavior();
            r.id = c.getInt(CNUM_ID);
            r.name = c.getString(CNUM_NAME);
            behaviors.add(r);
            c.moveToNext();
        }
        return behaviors.toArray(new Behavior[0]);
    }

    private static ContentValues behavior2ContentValues(Behavior r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_NAME, r.name);
        return cv;
    }

}

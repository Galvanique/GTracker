package galvanique.db.dao;

import android.content.Context;
import android.database.Cursor;

import galvanique.db.entities.Mood;

public class MoodDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------

    public static String TABLE_NAME = "mood";

    public static final String TAG = "MoodDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_NAME = "name";

    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_NAME
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_NAME = 1;

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CNAME_NAME + " TEXT" +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public MoodDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public Mood getMoodById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return cursor2mood(c);
    }

    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static Mood cursor2mood(Cursor c) {
        c.moveToFirst();
        Mood r = new Mood();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }

}

package galvanique.db.dao;

import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

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

    public String[] getAllMoodNames() {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                null
        );
        Mood[] moods = cursor2moods(c);
        List<String> names = new LinkedList<>();
        for (Mood m : moods) {
            names.add(m.name);
        }
        return names.toArray(new String[0]);
    }

    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    public static Mood cursor2mood(Cursor c) {
        c.moveToFirst();
        Mood r = new Mood();
        r.id = c.getInt(CNUM_ID);
        r.name = c.getString(CNUM_NAME);
        return r;
    }

    private static Mood[] cursor2moods(Cursor c) {
        c.moveToFirst();
        LinkedList<Mood> moods = new LinkedList<Mood>();
        while (!c.isAfterLast()) {
            Mood r = new Mood();
            r.id = c.getInt(CNUM_ID);
            r.name = c.getString(CNUM_NAME);
            moods.add(r);
            c.moveToNext();
        }
        return moods.toArray(new Mood[0]);
    }

}

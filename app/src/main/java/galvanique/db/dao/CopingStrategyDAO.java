package galvanique.db.dao;

import android.content.Context;
import android.database.Cursor;

import galvanique.db.entities.CopingStrategy;

public class CopingStrategyDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------

    public static String TABLE_NAME = "copingStrategy";

    public static final String TAG = "CopingStrategyDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_NAME = "name";
    public static final String CNAME_DESCRIPTION = "description";
    public static final String CNAME_DURATION = "duration";

    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_NAME,
            CNAME_DESCRIPTION,
            CNAME_DURATION
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_NAME = 1;
    public final static int CNUM_DESCRIPTION = 2;
    public final static int CNUM_DURATION = 3;

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CNAME_NAME + " TEXT," +
            CNAME_DESCRIPTION + " TEXT," +
            CNAME_DURATION + " INTEGER" +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_NAME = CNAME_NAME + "=?";

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

    public int getCopingStrategyByString(String s) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_NAME,
                new String[]{s + ""},
                null,
                null,
                null);
        return cursor2copingStrategy(c).id;
    }

    public CopingStrategy getRandomCopingStrategy() { //changed this to parameterless
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                "RANDOM()",
                "1");
        return cursor2copingStrategy(c);
    }


    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    protected static CopingStrategy cursor2copingStrategy(Cursor c) {
        c.moveToFirst();
        CopingStrategy r = new CopingStrategy(
                c.getString(CNUM_NAME),
                c.getString(CNUM_DESCRIPTION),
                c.getInt(CNUM_DURATION)
        );
        return r;
    }
}

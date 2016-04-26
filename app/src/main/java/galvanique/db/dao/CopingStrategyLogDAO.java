package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLog;
import galvanique.db.entities.CopingStrategyLogDefault;

public class CopingStrategyLogDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------
    private final int THRESHOLD = 5;


    public static String TABLE_NAME = "copingStrategyLog";

    public static final String TAG = "CopingStrategyLogDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_MOODLOGID = "moodLogID";
    public static final String CNAME_COPINGSTRATEGYID = "copingStrategyID";
    public static final String CNAME_EFFECTIVENESS = "effectiveness";
    public static final String CNAME_TIMESTAMP = "timestamp";


    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_MOODLOGID,
            CNAME_COPINGSTRATEGYID,
            CNAME_EFFECTIVENESS,
            CNAME_TIMESTAMP
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_MOODLOGID = 1;
    public final static int CNUM_COPINGSTRATEGYID = 2;
    public final static int CNUM_EFFECTIVENESS = 3;
    public final static int CNUM_TIMESTAMP = 4;


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CNAME_MOODLOGID + " INTEGER UNIQUE, " + //REMOVE CONSTRAINT?
            CNAME_COPINGSTRATEGYID + " INTEGER, " +
            CNAME_EFFECTIVENESS + " INTEGER, " +
            CNAME_TIMESTAMP + " INTEGER" +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_ML_ID = CNAME_MOODLOGID + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public CopingStrategyLogDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------
    //http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html

    public CopingStrategyLog getCopingStrategyLogById(int id) { //changed name
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

    public boolean checkIfMoodLogHasStrategy(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ML_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return !(c.getCount() == 0); // True if there exist logs
    }

    public CopingStrategyLog[] getAllCopingStrategyLogs() { //changed name
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                CNAME_ID + " DESC");
        return cursor2copingStrategies(c);
    }

    public long getCountCopingStrategyLogs() {
        Log.d("cslogs numentries", DatabaseUtils.queryNumEntries(db, TABLE_NAME) + "");
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public long getCountCopingStrategyLogsByMood(int moodID) {
        return db.rawQuery(
                "SELECT * FROM copingStrategyLog " +
                "JOIN moodLog ON copingStrategyLog.moodLogID = moodLog._id " +
                "WHERE moodLog.mood=" + moodID,
                null
        ).getCount();
    }

    public CopingStrategyLog getMostRecentLog() {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                "timestamp DESC",
                "1"
        );
        return cursor2copingStrategy(c);
    }

    public String[] getBestCopingStrategyNamesByMood(int moodID) {
        Cursor c;
        if (getCountCopingStrategyLogsByMood(moodID) > THRESHOLD) {
            // Get average rating for each coping strategy by mood from this table
            String QUERY = "SELECT strategy.name " +
                    "FROM copingStrategyLog clog " +
                    "JOIN copingStrategy strategy ON clog.copingStrategyID = strategy._id " +
                    "JOIN moodLog mlog ON mlog._id = clog.moodLogID " +
                    "JOIN mood ON mlog.mood = mood._id " +
                    "WHERE mood._id =" + moodID + " " +
                    "GROUP BY strategy.name, strategy.description, strategy.duration " +
                    "ORDER BY avg(clog.effectiveness) DESC;";
            c = db.rawQuery(QUERY, null);
        } else {
            // Get average rating for each coping strategy by mood from this table
            String QUERY = "SELECT strategy.name " +
                    "FROM copingStrategyLogDefault clog " +
                    "JOIN copingStrategy strategy ON clog.copingStrategyID = strategy._id " +
                    "JOIN mood ON clog.moodID = mood._id " +
                    "WHERE mood._id =" + moodID + " " +
                    "GROUP BY strategy.name, strategy.description, strategy.duration " +
                    "ORDER BY avg(clog.effectiveness) DESC;";
            c = db.rawQuery(QUERY, null);
        }
        List<String> names = new LinkedList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            names.add(c.getString(0));
            c.moveToNext();
        }
        return names.toArray(new String[names.size()]);
    }

    // --------------------------------------------
    // UPDATES
    // --------------------------------------------


    public void insert(CopingStrategyLog r) {
        ContentValues cv = copingStrategy2ContentValues(r);
        db.insert(TABLE_NAME, null, cv);
    }

    public void updateEffectiveness(CopingStrategyLog r, int effectiveness) {
        Log.d("updating id " + r.getId(), "with effectiveness " + effectiveness);
        ContentValues newValues = new ContentValues();
        newValues.put(CNAME_EFFECTIVENESS, effectiveness);
        db.update(TABLE_NAME, newValues, "_id=" + r.getId(), null);
    }

    public void delete(CopingStrategyLog r) {
        Log.d(TAG, "delete report " + r.id);
        db.delete(TABLE_NAME, WHERE_ID, new String[]{r.id + ""});
    }

    public void deleteAll() {
        Log.d(TAG, "delete all from " + TABLE_NAME);
        db.delete(TABLE_NAME, null, null);
    }

    // --------------------------------------------
    // COPINGSTRATEGY-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static CopingStrategyLog cursor2copingStrategy(Cursor c) {
        c.moveToFirst();
        CopingStrategyLog r = new CopingStrategyLog(
                c.getInt(CNUM_ID), c.getInt(CNUM_MOODLOGID), c.getInt(CNUM_COPINGSTRATEGYID),
                c.getInt(CNUM_EFFECTIVENESS),
                c.getLong(CNUM_TIMESTAMP));
        return r;
    }

    public static CopingStrategyLog[] cursor2copingStrategies(Cursor c) {
        c.moveToFirst();
        LinkedList<CopingStrategyLog> copingStrategies = new LinkedList<CopingStrategyLog>();
        while (!c.isAfterLast()) {
            CopingStrategyLog r = new CopingStrategyLog();
            r.id = c.getInt(CNUM_ID);
            r.moodLogID = c.getInt(CNUM_MOODLOGID);
            r.copingStrategyID = c.getInt(CNUM_COPINGSTRATEGYID);
            r.effectiveness = c.getInt(CNUM_EFFECTIVENESS);
            r.timestamp = c.getLong(CNUM_TIMESTAMP);
            copingStrategies.add(r);
            c.moveToNext();
        }
        return copingStrategies.toArray(new CopingStrategyLog[copingStrategies.size()]);
    }

    private static ContentValues copingStrategy2ContentValues(CopingStrategyLog r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_MOODLOGID, r.moodLogID);
        cv.put(CNAME_COPINGSTRATEGYID, r.copingStrategyID);
        cv.put(CNAME_EFFECTIVENESS, r.effectiveness);
        cv.put(CNAME_TIMESTAMP, r.timestamp);
        return cv;
    }

    public static String getISOTimeString(long time) {
        Calendar gc = GregorianCalendar.getInstance();
        gc.setTimeInMillis(time);
        String AM = "AM";
        int day = gc.get(Calendar.DAY_OF_MONTH);
        String ds = (day < 10 ? "0" : "") + day;
        int month = (gc.get(Calendar.MONTH) + 1);
        String ms = (month < 10 ? "0" : "") + month;
        int hour = gc.get(Calendar.HOUR_OF_DAY);
        String hs = "";
        if (hour >= 12) {
            AM = "PM";
            if (hour > 12) hour = hour - 12;
        }
        hs = (hour < 10 ? "0" : "") + hour;
        int min = gc.get(Calendar.MINUTE);
        String mins = (min < 10 ? "0" : "") + min;
        String s = gc.get(Calendar.YEAR) + "-" + ms + "-" + ds + " " + hs + ":" + mins + " " + AM;
        return s;
    }

}


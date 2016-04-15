package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import galvanique.db.entities.CopingStrategyLog;

public class CopingStrategyLogDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------
    // TODO-someone when is the table full enough to use user data?
    private final int THRESHOLD = 10;


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
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public CopingStrategyLog getMostRecentLog() {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                "timestamp DESC"
        );
        return cursor2copingStrategy(c);
    }

    public String[] getBestCopingStrategyNamesByMood(int moodID) {
        Cursor c;
        if (this.getCountCopingStrategyLogs() > THRESHOLD) {
            // Get average rating for each coping strategy by mood from this table
            final String QUERY = "SELECT mood.name, strat.name, AVG(clog.effectiveness) " +
                    "FROM copingStrategy strat " +
                    "JOIN copingStrategyLog clog ON clog.copingStrategyID=strat._id " +
                    "JOIN moodLog mlog ON clog.moodLogID=mlog._id " +
                    "JOIN mood ON mood._id = mlog.mood " +
                    "GROUP BY mood._id, clog.copingStrategyID " +
                    "ORDER BY AVG(clog.effectiveness) DESC;";
            db.rawQuery(QUERY, new String[]{CNAME_MOODLOGID});
        } else {
            // Get average rating for each coping strategy by mood from this table
            final String QUERY = "SELECT mood.name, strat.name, AVG(clog.effectiveness) " +
                    "FROM copingStrategy strat " +
                    "JOIN copingStrategyLogDefault clog ON clog.copingStrategyID=strat._id " +
                    "JOIN moodLog mlog ON clog.moodLogID=mlog._id " +
                    "JOIN mood ON mood._id = mlog.mood " +
                    "GROUP BY mood._id, clog.copingStrategyID " +
                    "ORDER BY AVG(clog.effectiveness) DESC;";
            db.rawQuery(QUERY, new String[]{CNAME_MOODLOGID});
        }
        /*
        CopingStrategyLog[] logs = cursor2copingStrategies(c);
        List<String> names = new LinkedList<>();
        // TODO get each coping strategy name from each log and add to names
        */
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    // --------------------------------------------
    // UPDATES
    // --------------------------------------------


    public void insert(CopingStrategyLog r) {
        ContentValues cv = copingStrategy2ContentValues(r);
        db.insert(TABLE_NAME, null, cv);
    }

    public void update(CopingStrategyLog r) {
        ContentValues values = copingStrategy2ContentValues(r);
        db.update(TABLE_NAME, values, WHERE_ID, new String[]{r.id + ""});
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
            CopingStrategyLog r = new CopingStrategyLog(
                    c.getInt(CNUM_ID), c.getInt(CNUM_MOODLOGID), c.getInt(CNUM_COPINGSTRATEGYID),
                    c.getInt(CNUM_EFFECTIVENESS),
                    c.getLong(CNUM_TIMESTAMP));
            copingStrategies.add(r);
            c.moveToNext();
        }
        return copingStrategies.toArray(new CopingStrategyLog[0]);
    }

    private static ContentValues copingStrategy2ContentValues(CopingStrategyLog r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_ID, r.id);
        cv.put(CNAME_MOODLOGID, r.moodLogID);
        cv.put(CNAME_TIMESTAMP, r.timestamp);
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


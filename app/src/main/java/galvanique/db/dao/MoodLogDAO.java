package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import galvanique.db.entities.MoodLog;

public class MoodLogDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------

    public static String TABLE_NAME = "moodLog";

    public static final String TAG = "MoodLogDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_TIMESTAMP = "timestamp";
    public static final String CNAME_MOOD = "mood";
    public static final String CNAME_BELIEF = "belief";
    public static final String CNAME_TRIGGER = "trig";
    public static final String CNAME_BEHAVIOR = "behavior";
    public static final String CNAME_MAGNITUDE = "magnitude";
    public static final String CNAME_COMMENTS = "comments";


    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_TIMESTAMP,
            CNAME_MOOD,
            CNAME_BELIEF,
            CNAME_TRIGGER,
            CNAME_BEHAVIOR,
            CNAME_MAGNITUDE,
            CNAME_COMMENTS
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_TIMESTAMP = 1;
    public final static int CNUM_MOOD = 2;
    public final static int CNUM_BELIEF = 3;
    public final static int CNUM_TRIGGER = 4;
    public final static int CNUM_BEHAVIOR = 5;
    public final static int CNUM_MAGNITUDE = 6;
    public final static int CNUM_COMMENTS = 7;


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CNAME_TIMESTAMP + " LONG, " +
            CNAME_MOOD + " INTEGER, " +
            CNAME_BELIEF + " INTEGER, " +
            CNAME_TRIGGER + " INTEGER, " +
            CNAME_BEHAVIOR + " INTEGER, " +
            CNAME_MAGNITUDE + " INTEGER, " +
            CNAME_COMMENTS + " TEXT" +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_TIME_RANGE = CNAME_TIMESTAMP + ">=?" + " AND " + CNAME_TIMESTAMP + "<=?";
    private final static String WHERE_TYPE = CNAME_MOOD + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public MoodLogDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public MoodLog getMoodLogById(int id) {
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

    public MoodLog[] getMoodByTimeRange(long startTime, long endTime) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_TIME_RANGE,
                new String[]{startTime + "", endTime + ""},
                null,
                null,
                null);
        return cursor2moods(c);
    }

    public MoodLog[] getAllMoods() {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                CNAME_TIMESTAMP + " DESC");
        return cursor2moods(c);
    }

    public MoodLog[] getMoodByType(int type) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_TYPE,
                new String[]{type + ""},
                null,
                null,
                null);
        return cursor2moods(c);
    }

    public MoodLog getMostRecentLog() {
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
        return cursor2mood(c);
    }

    public long getCountMoodLogs() {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public MoodLog[] getMoodsOverTime() {
        Cursor c;
        final String QUERY = "SELECT * FROM moodLog ORDER BY mood, timestamp ASC";
        c = db.rawQuery(QUERY, null);
        return cursor2moods(c);
    }

    // --------------------------------------------
    // UPDATES
    // --------------------------------------------


    public void insert(MoodLog r) {
        ContentValues cv = mood2ContentValues(r);
        db.insert(TABLE_NAME, null, cv);
    }

    public void update(MoodLog r) {
        ContentValues values = mood2ContentValues(r);
        db.update(TABLE_NAME, values, WHERE_ID, new String[]{r.id + ""});
    }

    public void delete(MoodLog r) {
        Log.d(TAG, "delete report " + r.id);
        db.delete(TABLE_NAME, WHERE_ID, new String[]{r.id + ""});
    }

    public void deleteAll() {
        Log.d(TAG, "delete all from " + TABLE_NAME);
        db.delete(TABLE_NAME, null, null);
    }

    // --------------------------------------------
    // MOOD-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static MoodLog cursor2mood(Cursor c) {
        c.moveToFirst();
        MoodLog r = new MoodLog();
        r.id = c.getInt(CNUM_ID);
        r.timestamp = c.getLong(CNUM_TIMESTAMP);
        r.moodID = c.getInt(CNUM_MOOD);
        r.beliefID = c.getInt(CNUM_BELIEF);
        r.triggerID = c.getInt(CNUM_TRIGGER);
        r.behaviorID = c.getInt(CNUM_BEHAVIOR);
        r.magnitude = c.getInt(CNUM_MAGNITUDE);
        r.comments = c.getString(CNUM_COMMENTS);
        return r;
    }

    public static MoodLog[] cursor2moods(Cursor c) {
        c.moveToFirst();
        LinkedList<MoodLog> moods = new LinkedList<MoodLog>();
        while (!c.isAfterLast()) {
            MoodLog r = new MoodLog();
            r.id = c.getInt(CNUM_ID);
            r.timestamp = c.getLong(CNUM_TIMESTAMP);
            r.moodID = c.getInt(CNUM_MOOD);
            r.beliefID = c.getInt(CNUM_BELIEF);
            r.triggerID = c.getInt(CNUM_TRIGGER);
            r.behaviorID = c.getInt(CNUM_BEHAVIOR);
            r.magnitude = c.getInt(CNUM_MAGNITUDE);
            r.comments = c.getString(CNUM_COMMENTS);
            moods.add(r);
            c.moveToNext();
        }
        return moods.toArray(new MoodLog[0]);
    }

    private static ContentValues mood2ContentValues(MoodLog r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_TIMESTAMP, r.timestamp);
        cv.put(CNAME_MOOD, r.moodID);
        cv.put(CNAME_BELIEF, r.beliefID);
        cv.put(CNAME_TRIGGER, r.triggerID);
        cv.put(CNAME_BEHAVIOR, r.behaviorID);
        cv.put(CNAME_MAGNITUDE, r.magnitude);
        cv.put(CNAME_COMMENTS, r.comments);
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

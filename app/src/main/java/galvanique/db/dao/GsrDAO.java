package galvanique.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import galvanique.db.entities.GsrLog;

public class GsrDAO extends GeneralDAO {

    // TODO-tyler associate with mood logs and visualize somewhere; add ability to export contents of table

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------

    public static String TABLE_NAME = "gsr";

    public static final String TAG = "GsrDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_TIMESTAMP = "timestamp";
    public static final String CNAME_CONDUCTIVITY = "conductivity";


    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_TIMESTAMP,
            CNAME_CONDUCTIVITY
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_TIMESTAMP = 1;
    public final static int CNUM_CONDUCTIVITY = 2;


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CNAME_TIMESTAMP + " INTEGER, " +
            CNAME_CONDUCTIVITY + " INTEGER " +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_TIME_RANGE = CNAME_TIMESTAMP + ">=?" + " AND " + CNAME_TIMESTAMP + "<=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public GsrDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public GsrLog getGsrById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id + ""},
                null,
                null,
                null);
        return cursor2gsr(c);
    }

    public GsrLog[] getGsrByTimeRange(long startTime, long endTime) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_TIME_RANGE,
                new String[]{startTime + "", endTime + ""},
                null,
                null,
                null);
        return cursor2gsrs(c);
    }

    public GsrLog[] getAllGsr() {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                CNAME_TIMESTAMP + " DESC");
        return cursor2gsrs(c);
    }
    public long getCountGsrLogs() {
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    // --------------------------------------------
    // UPDATES
    // --------------------------------------------


    public void insert(GsrLog r) {
        ContentValues cv = gsr2ContentValues(r);
        db.insert(TABLE_NAME, null, cv);
    }

    public void update(GsrLog r) {
        ContentValues values = gsr2ContentValues(r);
        db.update(TABLE_NAME, values, WHERE_ID, new String[]{r.id + ""});
    }

    public void delete(GsrLog r) {
        Log.d(TAG, "delete report " + r.id);
        db.delete(TABLE_NAME, WHERE_ID, new String[]{r.id + ""});
    }

    public void deleteAll() {
        Log.d(TAG, "delete all from " + TABLE_NAME);
        db.delete(TABLE_NAME, null, null);
    }

    // --------------------------------------------
    // GSR-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static GsrLog cursor2gsr(Cursor c) {
        c.moveToFirst();
        GsrLog r = new GsrLog();
        r.id = c.getInt(CNUM_ID);
        r.timestamp = c.getLong(CNUM_TIMESTAMP);
        r.conductivity = c.getLong(CNUM_CONDUCTIVITY);
        return r;
    }

    public static GsrLog[] cursor2gsrs(Cursor c) {
        c.moveToFirst();
        LinkedList<GsrLog> gsrs = new LinkedList<GsrLog>();
        while (!c.isAfterLast()) {
            GsrLog r = new GsrLog();
            r.id = c.getInt(CNUM_ID);
            r.timestamp = c.getLong(CNUM_TIMESTAMP);
            r.conductivity = c.getLong(CNUM_CONDUCTIVITY);
            gsrs.add(r);
            c.moveToNext();
        }
        return gsrs.toArray(new GsrLog[0]);
    }

    private static ContentValues gsr2ContentValues(GsrLog r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_TIMESTAMP, r.timestamp);
        cv.put(CNAME_CONDUCTIVITY, r.conductivity);
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

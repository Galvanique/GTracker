package galvanique.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Data access object for reports
 */
public class CopingStrategyDAO extends GeneralDAO {

    // --------------------------------------------
    // SCHEMA
    // --------------------------------------------

    public static String TABLE_NAME = "copingStrategies";

    public static final String TAG = "CopingStrategyDAO";

    public static final String CNAME_ID = "_id";
    public static final String CNAME_TIMESTAMP = "timestamp";
    public static final String CNAME_COPINGSTRATEGY = "copingStrategy";
    public static final String CNAME_LONGTERM = "longTerm";
    public static final String CNAME_EFFECTIVENESS = "effectiveness";


    public static final String[] PROJECTION = {
            CNAME_ID,
            CNAME_TIMESTAMP,
            CNAME_COPINGSTRATEGY,
            CNAME_LONGTERM,
            CNAME_EFFECTIVENESS
    };

    public final static int CNUM_ID = 0;
    public final static int CNUM_TIMESTAMP = 1;
    public final static int CNUM_COPINGSTRATEGY = 2;
    public final static int CNUM_LONGTERM = 3;
    public final static int CNUM_EFFECTIVENESS = 4;


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            CNAME_ID + " INTEGER PRIMARY KEY, " +
            CNAME_TIMESTAMP + " LONG, " +
            CNAME_COPINGSTRATEGY + " TEXT, " +
            CNAME_LONGTERM + " INTEGER, " +
            CNAME_EFFECTIVENESS + " INTEGER " +
            ");";

    // --------------------------------------------
    // QUERIES
    // --------------------------------------------

    private final static String WHERE_ID = CNAME_ID + "=?";
    private final static String WHERE_TIME_RANGE = CNAME_TIMESTAMP + ">=?"+" AND "+CNAME_TIMESTAMP + "<=?";
    private final static String WHERE_LONGTERM = CNAME_LONGTERM + "=?";

    // --------------------------------------------
    // LIVECYCLE
    // --------------------------------------------

    public CopingStrategyDAO(Context context) {
        super(context);
    }

    // --------------------------------------------
    // QUERY IMPLEMENTATIONS
    // --------------------------------------------

    public CopingStrategyLog getCopingStrategyById(int id) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_ID,
                new String[]{id+""},
                null,
                null,
                null);
        return cursor2copingStrategy(c);
    }

    public CopingStrategyLog[] getCopingStrategyByTimeRange(long startTime, long endTime) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_TIME_RANGE,
                new String[]{startTime+"",endTime+""},
                null,
                null,
                null);
        return cursor2copingStrategies(c);
    }

    public CopingStrategyLog[] getAllCopingStrategies() {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                null,
                null,
                null,
                null,
                CNAME_TIMESTAMP+" DESC");
        return cursor2copingStrategies(c);
    }

    public CopingStrategyLog getRandomCopingStrategy(int longTerm) {
        Cursor c = db.query(
                TABLE_NAME,
                PROJECTION,
                WHERE_LONGTERM,
                new String[]{longTerm+""},
                null,
                null,
                "RANDOM()",
                "1");
        return cursor2copingStrategy(c);
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
        db.update(TABLE_NAME, values , WHERE_ID, new String[]{r.id+""});
    }

    public void delete(CopingStrategyLog r) {
        Log.d(TAG, "delete report " + r.id);
        db.delete(TABLE_NAME, WHERE_ID, new String[]{r.id+""});
    }

    public void deleteAll() {
        Log.d(TAG,"delete all from " + TABLE_NAME);
        db.delete(TABLE_NAME, null, null);
    }

    // --------------------------------------------
    // COPINGSTRATEGY-CURSOR TRANSFORMATION UTILITIES
    // --------------------------------------------

    private static CopingStrategyLog cursor2copingStrategy(Cursor c) {
        c.moveToFirst();
        CopingStrategyLog r = new CopingStrategyLog();
        r.id = c.getInt(CNUM_ID);
        r.timestamp = c.getLong(CNUM_TIMESTAMP);
        r.copingStrategy = c.getString(CNUM_COPINGSTRATEGY);
        r.longTerm = c.getInt(CNUM_LONGTERM);
        r.effectiveness = c.getInt(CNUM_EFFECTIVENESS);
        return r;
    }

    public static CopingStrategyLog[] cursor2copingStrategies(Cursor c) {
        c.moveToFirst();
        LinkedList<CopingStrategyLog> copingStrategies = new LinkedList<CopingStrategyLog>();
        while(!c.isAfterLast()){
            CopingStrategyLog r = new CopingStrategyLog();
            r.id = c.getInt(CNUM_ID);
            r.timestamp = c.getLong(CNUM_TIMESTAMP);
            r.copingStrategy = c.getString(CNUM_COPINGSTRATEGY);
            r.longTerm = c.getInt(CNUM_LONGTERM);
            r.effectiveness = c.getInt(CNUM_EFFECTIVENESS);
            copingStrategies.add(r);
            c.moveToNext();
        }
        return copingStrategies.toArray(new CopingStrategyLog[0]);
    }

    private static ContentValues copingStrategy2ContentValues(CopingStrategyLog r) {
        ContentValues cv = new ContentValues();
        cv.put(CNAME_TIMESTAMP, r.timestamp);
        cv.put(CNAME_COPINGSTRATEGY, r.copingStrategy);
        cv.put(CNAME_LONGTERM, r.longTerm);
        cv.put(CNAME_EFFECTIVENESS, r.effectiveness);
        return cv;
    }

    public static String getISOTimeString(long time) {
        Calendar gc = GregorianCalendar.getInstance();
        gc.setTimeInMillis(time);
        String AM = "AM";
        int day = gc.get(Calendar.DAY_OF_MONTH);
        String ds = (day<10?"0":"")+day;
        int month = (gc.get(Calendar.MONTH)+1);
        String ms = (month<10?"0":"")+month;
        int hour = gc.get(Calendar.HOUR_OF_DAY);
        String hs = "";
        if(hour>=12){ AM = "PM"; if(hour>12) hour = hour-12;}
        hs = (hour<10?"0":"")+hour;
        int min = gc.get(Calendar.MINUTE);
        String mins = (min<10?"0":"")+min;
        String s = gc.get(Calendar.YEAR)+"-"+ms+"-"+ds+" "+hs+":"+mins+" "+AM;
        return s;
    }

}

package galvanique.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import galvanique.db.dao.BehaviorDAO;
import galvanique.db.dao.BeliefDAO;
import galvanique.db.dao.CopingStrategyDAO;
import galvanique.db.dao.CopingStrategyLogDAO;
import galvanique.db.dao.CopingStrategyLogDefaultDAO;
import galvanique.db.dao.GeneralDAO;
import galvanique.db.dao.GsrDAO;
import galvanique.db.dao.MoodDAO;
import galvanique.db.dao.MoodLogDAO;
import galvanique.db.dao.TriggerDAO;
import galvanique.db.entities.MoodLog;

import java.util.LinkedList;
import galvanique.db.entities.CopingStrategy;
import galvanique.db.entities.CopingStrategyLogDefault;
/**
 * This is the basic component to create and manage the database.
 * This class should only be used in the {@link GeneralDAO}.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventreports";
    private static final String TAG = "MyDBHelper";

    public MyDBHelper(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public MyDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.d(TAG, "database initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO-someone prepopulate any values we want already in the static cslogdefault and mood tables
        // Behavior
        db.execSQL(BehaviorDAO.TABLE_CREATE);
        Log.d(TAG, "table " + BehaviorDAO.TABLE_NAME + " was created");
        // Belief
        db.execSQL(BeliefDAO.TABLE_CREATE);
        Log.d(TAG, "table " + BeliefDAO.TABLE_NAME + " was created");
        // CopingStrategy
        db.execSQL(CopingStrategyDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyDAO.TABLE_NAME + " was created");

        LinkedList<CopingStrategy> strategies = new LinkedList<>();
        strategies.add(new CopingStrategy("Drink tea", "make tea and drink it", 10));
        strategies.add(new CopingStrategy("Go for a walk", "go out the door and keep walking", 10));
        strategies.add(new CopingStrategy("Watch a funny video", "Watch a comedy, or watch funny cat videos on YouTube", 10));
        strategies.add(new CopingStrategy("Call a friend", "go out the door and keep walking", 10));
        strategies.add(new CopingStrategy("Look at photos", "look at photos of friends, family, or pets", 10));
        strategies.add(new CopingStrategy("Aromatherapy", "scented candles, hand cream", 10));
        strategies.add(new CopingStrategy("Take a shower or relaxing bath", "go out the door and keep walking", 10));
        strategies.add(new CopingStrategy("Color in a coloring book", "scented candles", 10));
        strategies.add(new CopingStrategy("Read comic strips", "scented candles", 10));
        strategies.add(new CopingStrategy("Exercise", "Go for a run", 10));
        strategies.add(new CopingStrategy("Read a book", "or magazine", 10));
        strategies.add(new CopingStrategy("Knit or sew", "Work on a project for yourself or a friend", 10));
        strategies.add(new CopingStrategy("Take a short nap", "take a 20-minute nap", 10));
        strategies.add(new CopingStrategy("Meditate", "scented candles", 10));
        strategies.add(new CopingStrategy("Journal", "scented candles", 10));
        strategies.add(new CopingStrategy("Look at a journal of past achievements", "scented candles", 10));
        strategies.add(new CopingStrategy("Punch a punching bag", "Transfer your energy", 10));
        strategies.add(new CopingStrategy("Listen to music", "Listen to a playlist of your favorite songs", 10));
        strategies.add(new CopingStrategy("Deep breathing", "Concentrate on breathing in and out", 10));
        strategies.add(new CopingStrategy("Go to a public place", "Go to a coffee shop or the park", 10));

        for (CopingStrategy s : strategies) {
            String ROW = "INSERT INTO " + CopingStrategyDAO.TABLE_NAME + " ("
                    + CopingStrategyDAO.CNAME_NAME + "," + CopingStrategyDAO.CNAME_DESCRIPTION + ","
                    + CopingStrategyDAO.CNAME_DURATION + ") Values ('" + s.name + "','" + s.description
                    + "','" + s.duration + "')";
            db.execSQL(ROW);
        }

        // CopingStrategyLog
        db.execSQL(CopingStrategyLogDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyLogDAO.TABLE_NAME + " was created");
        // CopingStrategyLogDefault
        db.execSQL(CopingStrategyLogDefaultDAO.TABLE_CREATE);
        Log.d(TAG, "table " + CopingStrategyLogDefaultDAO.TABLE_NAME + " was created");

        /*
        1 "drink tea" angry 5, anxious 4
        2 "go for a walk" sad 3, anxious 4, angry 5, depressed 6
        3 "watch a funny video" sad 3, anxious 4, angry 5, depressed 6
        4 "call a friend" sad 3, depressed 6, anxious 4
        5 "look at photos of friends and family" sad 3, depressed 6
        6 "aromatherapy, scented candles" anxious 4
        7 "take a shower or relaxing bath" anxious 4
        8 "color in a coloring book" anxious 4, depressed 6
        9 "read comic strips" anxious 4, sad 3, depressed 6
        10 "exercise" anxious 4, sad 3, depressed 6, shame 7
        11 "read a book" anxious 4, sad 3, depressed 6, shame 7
        12 "knit or sew" sad 3, depressed 6, anxious 4
        13 "take a 20-minute nap" anxious 4
        14 "meditate" sad 3, depressed 6, anxious 4, shame 7, angry 5
        15 "journal" sad 3, depressed 6, anxious 4, shame 7
        16 "look at a journal of past achievements" shame 7, depressed 6
        17 "punch a punching bag" angry 5, anxious 4
        18 "listen to a playlist of uplifting music" sad 3, depressed 6, shame 7
        19 "practice deep breathing" angry 5, anxious 4
        20 "go to a public place" depressed 6
        */

        LinkedList<CopingStrategyLogDefault> entries = new LinkedList<>();
        entries.add(new CopingStrategyLogDefault(5,1,1));
        entries.add(new CopingStrategyLogDefault(4,1,1));
        entries.add(new CopingStrategyLogDefault(3,2,1));
        entries.add(new CopingStrategyLogDefault(4,2,1));
        entries.add(new CopingStrategyLogDefault(5,2,1));
        entries.add(new CopingStrategyLogDefault(6,2,1));
        entries.add(new CopingStrategyLogDefault(3,3,1));
        entries.add(new CopingStrategyLogDefault(4,3,1));
        entries.add(new CopingStrategyLogDefault(5,3,1));
        entries.add(new CopingStrategyLogDefault(6,3,1));
        entries.add(new CopingStrategyLogDefault(3,4,1));
        entries.add(new CopingStrategyLogDefault(6,4,1));
        entries.add(new CopingStrategyLogDefault(4,4,1));
        entries.add(new CopingStrategyLogDefault(3,5,1));
        entries.add(new CopingStrategyLogDefault(6,5,1));
        entries.add(new CopingStrategyLogDefault(4,6,1));
        entries.add(new CopingStrategyLogDefault(4,7,1));
        entries.add(new CopingStrategyLogDefault(4,8,1));
        entries.add(new CopingStrategyLogDefault(6,8,1));
        entries.add(new CopingStrategyLogDefault(4,9,1));
        entries.add(new CopingStrategyLogDefault(3,9,1));
        entries.add(new CopingStrategyLogDefault(6,9,1));
        entries.add(new CopingStrategyLogDefault(4,10,1));
        entries.add(new CopingStrategyLogDefault(3,10,1));
        entries.add(new CopingStrategyLogDefault(6,10,1));
        entries.add(new CopingStrategyLogDefault(7,10,1));
        entries.add(new CopingStrategyLogDefault(4,11,1));
        entries.add(new CopingStrategyLogDefault(3,11,1));
        entries.add(new CopingStrategyLogDefault(6,11,1));
        entries.add(new CopingStrategyLogDefault(7,11,1));
        entries.add(new CopingStrategyLogDefault(3,12,1));
        entries.add(new CopingStrategyLogDefault(6,12,1));
        entries.add(new CopingStrategyLogDefault(4,12,1));
        entries.add(new CopingStrategyLogDefault(4,13,1));
        entries.add(new CopingStrategyLogDefault(3,14,1));
        entries.add(new CopingStrategyLogDefault(6,14,1));
        entries.add(new CopingStrategyLogDefault(4,14,1));
        entries.add(new CopingStrategyLogDefault(7,14,1));
        entries.add(new CopingStrategyLogDefault(5,14,1));
        entries.add(new CopingStrategyLogDefault(3,15,1));
        entries.add(new CopingStrategyLogDefault(6,15,1));
        entries.add(new CopingStrategyLogDefault(4,15,1));
        entries.add(new CopingStrategyLogDefault(7,15,1));
        entries.add(new CopingStrategyLogDefault(7,16,1));
        entries.add(new CopingStrategyLogDefault(6,16,1));
        entries.add(new CopingStrategyLogDefault(5,17,1));
        entries.add(new CopingStrategyLogDefault(4,17,1));
        entries.add(new CopingStrategyLogDefault(3,18,1));
        entries.add(new CopingStrategyLogDefault(6,18,1));
        entries.add(new CopingStrategyLogDefault(7,18,1));
        entries.add(new CopingStrategyLogDefault(5,19,1));
        entries.add(new CopingStrategyLogDefault(4,19,1));
        entries.add(new CopingStrategyLogDefault(6,20,1));

        for (CopingStrategyLogDefault e : entries) {
            String ROW = "INSERT INTO " + CopingStrategyLogDefaultDAO.TABLE_NAME + " ("
                    + CopingStrategyLogDefaultDAO.CNAME_MOODID + "," + CopingStrategyLogDefaultDAO.CNAME_COPINGSTRATEGYID + ","
                    + CopingStrategyLogDefaultDAO.CNAME_EFFECTIVENESS + ") Values ('" + e.moodID + "','" + e.copingStrategyID
                    + "','" + e.effectiveness + "')";
            db.execSQL(ROW);
        }


        // Gsr
        db.execSQL(GsrDAO.TABLE_CREATE);
        Log.d(TAG, "table " + GsrDAO.TABLE_NAME + " was created");
        // Mood
        db.execSQL(MoodDAO.TABLE_CREATE);
        Log.d(TAG, "table " + MoodDAO.TABLE_NAME + " was created");
        // Prepopulate static mood table
        // TODO-tyler this is really bad practice sorry
        MoodLog.Mood moods[] = MoodLog.Mood.values();
        String moodStrings[] = new String[moods.length];
        for (int i = 0; i < moods.length; i++) {
            moodStrings[i] = moods[i].name();
        }
        for (String mood : moodStrings) {
            String ROW = "INSERT INTO " + MoodDAO.TABLE_NAME + " ("
                    + MoodDAO.CNAME_NAME + ") Values ('" + mood + "')";
            db.execSQL(ROW);
        }
        // MoodLog
        db.execSQL(MoodLogDAO.TABLE_CREATE);
        Log.d(TAG, "table " + MoodLogDAO.TABLE_NAME + " was created");
        // Trigger
        db.execSQL(TriggerDAO.TABLE_CREATE);
        Log.d(TAG, "table " + TriggerDAO.TABLE_NAME + " was created");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Utils.d(this, "===========DROPPING DBs====== old: "+oldVersion+"====new: "+newVersion+"=========");

        // clear old schema and data
        //db.execSQL("DROP TABLE IF EXISTS " + ReportDAO.TABLE_NAME);

        // create new schema
        //onCreate(db);

    }

}
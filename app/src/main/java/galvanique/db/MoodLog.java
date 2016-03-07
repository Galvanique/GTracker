package galvanique.db;

import android.util.Log;

import java.util.Comparator;

/**
 * This class represents an event report
 */
public class MoodLog {

    private enum Mood {
        // TODO populate moods
    }

    /** Report id */
    public int id = -1;

    /** timestamp of the event */
    public long timestamp;

    public Mood mood;

    public String belief;
    public String trigger;
    public String behavior;
    public int magnitude;

    /**
     * Constructor without id
     * @param timestamp
     * @param belief
     * @param trigger
     * @param behavior
     * @param magnitude
     */

    public MoodLog(long timestamp, String belief, String trigger, String behavior, int magnitude) {
        this.timestamp = timestamp;
        this.belief = belief;
        this.trigger = trigger;
        this.behavior = behavior;
        this.magnitude = magnitude;
    }

    /**
     * Constructor with report id
     * @param id
     * @param timestamp
     * @param belief
     * @param trigger
     * @param behavior
     * @param magnitude
     */
    public MoodLog(int id, long timestamp, String belief, String trigger, String behavior, int magnitude) {
        this.id = id;
        this.timestamp = timestamp;
        this.belief = belief;
        this.trigger = trigger;
        this.behavior = behavior;
        this.magnitude = magnitude;
    }

    /**
     * Empty Constructor
     */
    public MoodLog() {

    }

    public boolean equals(MoodLog r){
        return this.id == r.id;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getBelief() { return belief; }

    public String getTrigger() { return trigger; }

    public String getBehavior() { return behavior; }

    public int getMagnitude() { return magnitude; }

    /*Comparator for sorting the list by timestamp*/
    public static Comparator<MoodLog> timeComparator = new Comparator<MoodLog>() {

        public int compare(MoodLog mood1, MoodLog mood2) {
            Long time1 = mood1.timestamp;
            Long time2 = mood2.timestamp;
            return time1.compareTo(time2);
        }
    };


}

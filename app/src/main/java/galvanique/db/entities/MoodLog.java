package galvanique.db.entities;

import java.util.Comparator;

//TODO add comments field

public class MoodLog {

    public enum Mood { // TODO real moods
        Happy, Sad, Anxious, Angry, Guilt, Shame, Depressed, Bored, Tired, Lonely, Proud, Hopeful,
        Frustrated, Disgust, Numb, PhysicalPain, IntrusiveThoughts, Stressed,
        Irritable, Motivated, Excited, Grateful, Joy, Loved
    }

    public int id = -1;
    public long timestamp;
    public int mood;
    public int belief;
    public int trigger;
    public int behavior;
    public int magnitude;
    public String comments;

    /**
     * Constructor without id
     *
     * @param timestamp
     * @param mood
     * @param belief
     * @param trigger
     * @param behavior
     * @param magnitude
     * @param comments
     */

    public MoodLog(long timestamp, Mood mood, int belief, int trigger, int behavior, int magnitude, String comments) {
        this.timestamp = timestamp;
        this.mood = mood.ordinal();
        this.belief = belief;
        this.trigger = trigger;
        this.behavior = behavior;
        this.magnitude = magnitude;
        this.comments = comments;
    }

    /**
     * Constructor with report id
     *
     * @param id
     * @param timestamp
     * @param mood
     * @param belief
     * @param trigger
     * @param behavior
     * @param magnitude
     * @param comments
     */
    public MoodLog(int id, long timestamp, Mood mood, int belief, int trigger, int behavior, int magnitude, String comments) {
        this.id = id;
        this.timestamp = timestamp;
        this.mood = mood.ordinal();
        this.belief = belief;
        this.trigger = trigger;
        this.behavior = behavior;
        this.magnitude = magnitude;
        this.comments = comments;
    }

    /**
     * Empty Constructor
     */
    public MoodLog() {

    }

    public boolean equals(MoodLog r) {
        return this.id == r.id;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMood() {
        return mood;
    }

    public String getMoodString() {
        return Mood.values()[mood].name();
    }

    public int getBelief() {
        return belief;
    }

    public int getTrigger() {
        return trigger;
    }

    public int getBehavior() {
        return behavior;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public String getComments() { return comments; }

    /*Comparator for sorting the list by timestamp*/
    public static Comparator<MoodLog> timeComparator = new Comparator<MoodLog>() {

        public int compare(MoodLog mood1, MoodLog mood2) {
            Long time1 = mood1.timestamp;
            Long time2 = mood2.timestamp;
            return time1.compareTo(time2);
        }
    };


}

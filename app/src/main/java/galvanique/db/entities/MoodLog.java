package galvanique.db.entities;

import java.util.Comparator;

public class MoodLog {

    public enum Mood {
        Happy, Sad, Anxious, Angry, Guilt, Shame, Depressed, Bored, Tired, Lonely, Proud, Hopeful,
        Frustrated, Disgust, Numb, PhysicalPain, IntrusiveThoughts, Stressed,
        Irritable, Motivated, Excited, Grateful, Joy, Loved
    }

    public int id = -1;
    public long timestamp;
    public int moodID;
    public int beliefID;
    public int triggerID;
    public int behaviorID;
    public int magnitude;
    public String comments;

    /**
     * Constructor with id
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
        this.moodID = mood.ordinal();
        this.beliefID = belief;
        this.triggerID = trigger;
        this.behaviorID = behavior;
        this.magnitude = magnitude;
        this.comments = comments;
    }

    /**
     * Constructor with report id
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
        this.moodID = mood.ordinal();
        this.beliefID = belief;
        this.triggerID = trigger;
        this.behaviorID = behavior;
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

    public int getMoodID() {
        return moodID;
    }

    public String getMoodString() {
        return Mood.values()[moodID].name();
    }

    public int getBelief() {
        return beliefID;
    }

    public int getTrigger() {
        return triggerID;
    }

    public int getBehavior() {
        return behaviorID;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public String getComments() {
        return comments;
    }

    /*Comparator for sorting the list by timestamp*/
    public static Comparator<MoodLog> timeComparator = new Comparator<MoodLog>() {

        public int compare(MoodLog mood1, MoodLog mood2) {
            Long time1 = mood1.timestamp;
            Long time2 = mood2.timestamp;
            return time1.compareTo(time2);
        }
    };


}

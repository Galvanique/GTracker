package galvanique.db.entities;

/**
 * Created by K on 4/6/16.
 */

import java.util.Comparator;

public class CopingStrategyLog {


    public int id = -1;
    public int moodLogID;
    public int copingStrategyID;
    public int effectiveness;
    public long timestamp;

    /**
     * Empty constructor
     */
    public CopingStrategyLog() {

    }

    /**
     * Constructor without id
     *
     * @param moodLogID
     * @param copingStrategyID
     * @param effectiveness
     * @param timestamp
     */

    public CopingStrategyLog(int moodLogID, int copingStrategyID, int effectiveness, long timestamp) {
        this.moodLogID = moodLogID;
        this.copingStrategyID = copingStrategyID;
        this.effectiveness = effectiveness;
        this.timestamp = timestamp;
    }

    /**
     * Constructor with report id
     *
     * @param id
     * @param moodLogID
     * @param copingStrategyID
     * @param effectiveness
     * @param timestamp
     */
    public CopingStrategyLog(int id, int moodLogID, int copingStrategyID, int effectiveness, long timestamp) {
        this.id = id;
        this.moodLogID = moodLogID;
        this.copingStrategyID = copingStrategyID;
        this.effectiveness = effectiveness;
        this.timestamp = timestamp;
    }


    public boolean equals(CopingStrategyLog r) {
        return this.id == r.id;
    }

    public int getId() {
        return id;
    }

    public int getCopingStrategyID() {
        return copingStrategyID;
    }

    public int getMoodLogID() {
        return moodLogID;
    }

    public int getEffectiveness() {
        return effectiveness;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setEffectiveness(int effectiveness) {
        this.effectiveness = effectiveness;
    }

    /*Comparator for sorting the list by timestamp*/
    public static Comparator<CopingStrategyLog> timeComparator = new Comparator<CopingStrategyLog>() {

        public int compare(CopingStrategyLog copingStrategy1, CopingStrategyLog copingStrategy2) {
            Long time1 = copingStrategy1.timestamp;
            Long time2 = copingStrategy2.timestamp;
            return time1.compareTo(time2);
        }
    };


}

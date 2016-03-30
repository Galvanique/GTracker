package galvanique.db.entities;

import java.util.Comparator;

public class CopingStrategyLog {

    public int id = -1;
    public long timestamp;
    public String copingStrategy;
    public int longTerm;
    public int effectiveness;

    /**
     * Constructor without id
     *
     * @param timestamp
     * @param copingStrategy
     * @param longTerm
     * @param effectiveness
     */

    public CopingStrategyLog(long timestamp, String copingStrategy, int longTerm, int effectiveness) {
        this.timestamp = timestamp;
        this.copingStrategy = copingStrategy;
        this.longTerm = longTerm;
        this.effectiveness = effectiveness;
    }

    /**
     * Constructor with report id
     *
     * @param id
     * @param timestamp
     * @param copingStrategy
     * @param longTerm
     * @param effectiveness
     */
    public CopingStrategyLog(int id, long timestamp, String copingStrategy, int longTerm, int effectiveness) {
        this.id = id;
        this.timestamp = timestamp;
        this.copingStrategy = copingStrategy;
        this.longTerm = longTerm;
        this.effectiveness = effectiveness;
    }

    /**
     * Empty Constructor
     */
    public CopingStrategyLog() {

    }

    public boolean equals(CopingStrategyLog r) {
        return this.id == r.id;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCopingStrategy() {
        return copingStrategy;
    }

    public int getLongTerm() {
        return longTerm;
    }

    public int getEffectiveness() {
        return effectiveness;
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

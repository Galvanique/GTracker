package galvanique.db;

import java.util.Comparator;

/**
 * This class represents an event report
 */
public class GsrLog {

    public int id = -1;
    public long timestamp;
    public long conductivity;

    /**
     * Constructor without id
     * @param timestamp
     * @param conductivity
     */

    public GsrLog(long timestamp, long conductivity) {
        this.timestamp = timestamp;
        this.conductivity = conductivity;
    }

    /**
     * Constructor with report id
     * @param id
     * @param timestamp
     * @param conductivity
     */
    public GsrLog(int id, long timestamp, long conductivity) {
        this.id = id;
        this.timestamp = timestamp;
        this.conductivity = conductivity;
    }

    /**
     * Empty Constructor
     */
    public GsrLog() {

    }

    public boolean equals(GsrLog r){
        return this.id == r.id;
    }

    public int getId() { return id; }

    public long getTimestamp() { return timestamp; }

    public long getConductivity() { return conductivity; }

    /*Comparator for sorting the list by timestamp*/
    public static Comparator<MoodLog> timeComparator = new Comparator<MoodLog>() {

        public int compare(MoodLog mood1, MoodLog mood2) {
            Long time1 = mood1.timestamp;
            Long time2 = mood2.timestamp;
            return time1.compareTo(time2);
        }
    };


}

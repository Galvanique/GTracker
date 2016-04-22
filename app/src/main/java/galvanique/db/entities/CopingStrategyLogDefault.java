package galvanique.db.entities;

/**
 * Created by K on 4/19/16.
 */
public class CopingStrategyLogDefault {
    public int id = -1;
    public int moodID;
    public int copingStrategyID;
    public int effectiveness;

    /**
     * Constructor without id
     *
     * @param moodID
     * @param copingStrategyID
     * @param effectiveness
     */

    public CopingStrategyLogDefault(int moodID, int copingStrategyID, int effectiveness) {
        this.moodID = moodID;
        this.copingStrategyID = copingStrategyID;
        this.effectiveness = effectiveness;
    }

    /**
     * Constructor with report id
     *
     * @param id
     * @param moodID
     * @param copingStrategyID
     * @param effectiveness
     */
    public CopingStrategyLogDefault(int id, int moodID, int copingStrategyID, int effectiveness) {
        this.id = id;
        this.moodID = moodID;
        this.copingStrategyID = copingStrategyID;
        this.effectiveness = effectiveness;
    }


    public boolean equals(CopingStrategyLogDefault r) {
        return this.id == r.id;
    }

    public int getId() {
        return id;
    }

    public int getCopingStrategyID() {
        return copingStrategyID;
    }

    public int getMoodLogID() {
        return moodID;
    }

    public int getEffectiveness() {
        return effectiveness;
    }

}

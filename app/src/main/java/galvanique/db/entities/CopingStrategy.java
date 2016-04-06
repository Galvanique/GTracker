package galvanique.db.entities;

public class CopingStrategy {

    public int id = -1;
    public String name;

    /**
     * Constructor without id
     *
     * @param name
     */

    public CopingStrategy(String name) {
        this.name= name;
    }

    /**
     * Constructor with report id
     *
     * @param id
     * @param name
     */

    public CopingStrategy(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Empty Constructor
     */

    public CopingStrategy() { }

    public boolean equals(Mood r) {
        return this.id == r.id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

package galvanique.db.entities;

public class Mood {

    public int id = -1;
    public String name;

    /**
     * Constructor without id
     *
     * @param name
     */

    public Mood(String name) {
        this.name= name;
    }

    /**
     * Constructor with report id
     *
     * @param id
     * @param name
     */

    public Mood(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Empty Constructor
     */

    public Mood() { }

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

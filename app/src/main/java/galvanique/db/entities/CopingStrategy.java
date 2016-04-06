package galvanique.db.entities;

public class CopingStrategy {

    public int id; //TODO do we need this field?
    public String name;
    public String description;
    public long duration;

    public CopingStrategy(String name, String description, long duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

}

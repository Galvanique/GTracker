package galvanique.db.entities;

public class CopingStrategy {

    public int id;
    public String name;
    public String description;
    public long duration;

    public CopingStrategy(int id, String name, String description, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public CopingStrategy(String name, String description, long duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

}

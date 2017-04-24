package nz.james.senappsproximityapplication;

/**
 * Created by james on 24/04/2017.
 */

public class Trigger {

    private String ID;
    private String Name;
    private String Type;
    private String Time;
    private String user;

    public Trigger(){

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

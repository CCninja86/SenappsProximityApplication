package nz.james.senappsproximityapplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by james on 18/03/2017.
 */

public class GimbalBeacon {

    private String id;
    private String factoryId;
    private String name;
    private Map<String, String> beacon_attributes;


    public GimbalBeacon(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return beacon_attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.beacon_attributes = attributes;
    }
}

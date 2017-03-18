package nz.james.senappsproximityapplication;

import java.util.HashMap;

/**
 * Created by james on 18/03/2017.
 */

public class GimbalPlace {
    private String id;
    private String name;
    private Geofence geofence;
    private GimbalBeacon[] beacons;
    private int arrivalRssi;
    private int departureRssi;
    private HashMap<String, String> attributes;

    public GimbalPlace(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geofence getGeofence() {
        return geofence;
    }

    public void setGeofence(Geofence geofence) {
        this.geofence = geofence;
    }

    public GimbalBeacon[] getBeacons() {
        return beacons;
    }

    public void setBeacons(GimbalBeacon[] beacons) {
        this.beacons = beacons;
    }

    public int getArrivalRssi() {
        return arrivalRssi;
    }

    public void setArrivalRssi(int arrivalRssi) {
        this.arrivalRssi = arrivalRssi;
    }

    public int getDepartureRssi() {
        return departureRssi;
    }

    public void setDepartureRssi(int departureRssi) {
        this.departureRssi = departureRssi;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }
}

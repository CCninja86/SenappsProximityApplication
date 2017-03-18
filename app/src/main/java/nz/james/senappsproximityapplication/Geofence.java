package nz.james.senappsproximityapplication;

/**
 * Created by james on 18/03/2017.
 */

public class Geofence {
    private String shape;
    private int radius;
    private int center;
    private Coordinates[] points;
    private String source;
    private String sourceID;
    private int sourceVersion;
    private String sourceIDRType;

    public Geofence(){

    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getCenter() {
        return center;
    }

    public void setCenter(int center) {
        this.center = center;
    }

    public Coordinates[] getPoints() {
        return points;
    }

    public void setPoints(Coordinates[] points) {
        this.points = points;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public int getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(int sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getSourceIDRType() {
        return sourceIDRType;
    }

    public void setSourceIDRType(String sourceIDRType) {
        this.sourceIDRType = sourceIDRType;
    }
}

package org.abondar.experimental.earthquakereporter;

/**
 * Created by abondar on 12/18/16.
 */
public class EarthQuake {

    private Double magnitude;
    private String location;
    private String Date;

    public EarthQuake(Double magnitude, String location, String date) {
        this.magnitude = magnitude;
        this.location = location;
        Date = date;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}

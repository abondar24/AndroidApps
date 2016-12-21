package org.abondar.experimental.earthquakereporter;

import java.util.Date;

/**
 * Created by abondar on 12/18/16.
 */
public class Earthquake {

    private Double magnitude;
    private String location;
    private java.util.Date time;
    private String url;



    public Earthquake(Double magnitude, String location, Date time, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.time = time;
        this.url = url;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

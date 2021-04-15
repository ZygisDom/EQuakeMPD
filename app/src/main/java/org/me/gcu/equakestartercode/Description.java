///////////////////////////////////////////////////////////////////////////////
//
// ZYGIMANTAS DOMARKAS
// S1718169
//
///////////////////////////////////////////////////////////////////////////////

package org.me.gcu.equakestartercode;

public class Description {
    String date, location, cords, depth, magnitude;

    public String getDate() {
        return date;
    }

    public String setDate(String date) {
        this.date = date;
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String setLocation(String location) {
        this.location = location;
        return location;
    }

    public String getCords() {
        return cords;
    }

    public void setCords(String cords) {
        this.cords = cords;
    }

    public String getDepth() {
        return depth;
    }

    public String setDepth(String depth) {
        this.depth = depth;
        return depth;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public String setMagnitude(String magnitude) {
        this.magnitude = magnitude;
        return magnitude;
    }
}

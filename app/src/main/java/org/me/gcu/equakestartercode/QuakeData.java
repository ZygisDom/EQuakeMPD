///////////////////////////////////////////////////////////////////////////////
//
// ZYGIMANTAS DOMARKAS
// S1718169
//
///////////////////////////////////////////////////////////////////////////////

package org.me.gcu.equakestartercode;

import android.os.Parcel;
import android.os.Parcelable;

public class QuakeData implements Parcelable {
    private String title, link, pubDate, category, geoLat, geoLong, depth, magnitude, location, eqDate;
    String description;
    private double magnitudeD;

    //setter description, do the splitting

    protected QuakeData(Parcel in) {
        title = in.readString();
        link = in.readString();
        pubDate = in.readString();
        category = in.readString();
        geoLat = in.readString();
        geoLong = in.readString();
        depth = in.readString();
        magnitude = in.readString();
        location = in.readString();
        eqDate = in.readString();
        description = in.readString();
    }

    public QuakeData(){
    }

    public double getMagnitudeD() {
        return Double.parseDouble(this.magnitude.split(" ")[3]);
    }

    public void setMagnitudeD(double magnitudeD) {
        this.magnitudeD = magnitudeD;
    }

    public static final Creator<QuakeData> CREATOR = new Creator<QuakeData>() {
        @Override
        public QuakeData createFromParcel(Parcel in) {
            return new QuakeData(in);
        }

        @Override
        public QuakeData[] newArray(int size) {
            return new QuakeData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGeoLat() {
        return geoLat;
    }

    public void setGeoLat(String geoLat) {
        this.geoLat = geoLat;
    }

    public String getGeoLong() {
        return geoLong;
    }

    public void setGeoLong(String geoLong) {
        this.geoLong = geoLong;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getEqDate() {
        return eqDate;
    }

    public void setEqDate(String EqDate) {
        this.eqDate = EqDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        String[] result = description.split(";");
        for(int i = 0; i < result.length; i++)  {
            if(i == 0) {
                setEqDate(result[0]);
            } else if (i == 1) {
                setLocation(result[1]);
            } else if (i == 3) {
                setDepth(result[3]);
            } else if (i == 4) {
                setMagnitude(result[4]);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(pubDate);
        dest.writeString(category);
        dest.writeString(geoLat);
        dest.writeString(geoLong);
        dest.writeString(depth);
        dest.writeString(magnitude);
        dest.writeString(location);
        dest.writeString(eqDate);
        dest.writeString(description);
    }
}

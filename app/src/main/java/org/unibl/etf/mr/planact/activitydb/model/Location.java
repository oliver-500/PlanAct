package org.unibl.etf.mr.planact.activitydb.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.android.volley.toolbox.JsonArrayRequest;

import org.unibl.etf.mr.planact.util.Constants;

import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_LOCATION)
public class Location {

    private String displayName;
    private double lon;
    private int id_activity;

    @PrimaryKey(autoGenerate = true)
    private long id_location;

    public long getId_location() {
        return id_location;
    }

    public void setId_location(long id_location) {
        this.id_location = id_location;
    }

    public Location(){

    }

    public String getDisplayName() {
        return displayName;
    }

    @Ignore
    public Location(String displayName, double lat, double lon, int id_activity) {
        this.displayName = displayName;
        this.lon = lon;
        this.lat = lat;
        this.id_activity = id_activity;
    }
    @Ignore
    public Location(String displayName, double lat, double lon) {
        this.displayName = displayName;
        this.lon = lon;
        this.lat = lat;

    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public double getLon() {
        return lon;
    }

    public int getId_activity() {
        return id_activity;
    }

    public void setId_activity(int id_activity) {
        this.id_activity = id_activity;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.lon, lon) == 0 && id_activity == location.id_activity && id_location == location.id_location && Double.compare(location.lat, lat) == 0 && Objects.equals(displayName, location.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, lon, id_activity, id_location, lat);
    }
}

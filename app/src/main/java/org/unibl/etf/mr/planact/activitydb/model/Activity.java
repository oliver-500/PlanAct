package org.unibl.etf.mr.planact.activitydb.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.unibl.etf.mr.planact.activitydb.enums.ActivityType;
import org.unibl.etf.mr.planact.util.Constants;

import java.io.Serializable;
import org.threeten.bp.LocalDateTime;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_ACTIVITY)

public class Activity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long activity_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return activity_id == activity.activity_id && name.equals(activity.getName()) && time.equals(activity.getTime()) && description.equals(activity.getDescription())
                && location.equals(activity.getLocation()) && type.equals(activity.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity_id, name, time, description, location, type);
    }



    public long getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(long activity_id) {
        this.activity_id = activity_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    @Ignore
    public Activity(){

    }

    public Activity(String name, LocalDateTime time, String description, String location, ActivityType type, Boolean isDone) {
        this.isDone = isDone;
        this.name = name;
        this.time = time;
        this.description = description;
        this.location = location;
        this.type = type;
    }


    private Boolean isDone;
    private String name;

    private LocalDateTime time;

    private String description;

    private String location;

    private ActivityType type;
}

package org.unibl.etf.mr.planact.activitydb.converters;

import androidx.room.TypeConverter;

import org.unibl.etf.mr.planact.activitydb.enums.ActivityType;

public class ActivityTypeConverter {
    @TypeConverter
    public static ActivityType fromString(String value) {
        return ActivityType.valueOf(value);
    }

    @TypeConverter
    public static String toString(ActivityType activityType) {
        return activityType.name();
    }
}

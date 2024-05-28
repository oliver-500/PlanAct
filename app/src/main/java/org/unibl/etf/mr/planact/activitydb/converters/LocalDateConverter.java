package org.unibl.etf.mr.planact.activitydb.converters;

import androidx.room.TypeConverter;

import org.threeten.bp.LocalDate;

public class LocalDateConverter {
    @TypeConverter
    public static LocalDate fromTimestamp(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}

package org.unibl.etf.mr.planact.activitydb.converters;

import androidx.room.TypeConverter;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class LocalDateTimeConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @TypeConverter
    public static LocalDateTime toLocalDateTime(String value) {
        return LocalDateTime.parse(value, formatter);
    }

    @TypeConverter
    public static String toTimestamp(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}

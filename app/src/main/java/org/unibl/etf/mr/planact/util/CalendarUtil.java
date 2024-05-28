package org.unibl.etf.mr.planact.util;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

public class CalendarUtil {
    public static String formatDateForView(String dateTime){

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDate date = null;
        LocalTime time = null;


        String dateString = dateTime.split("T")[0];
        String timeString = dateTime.split("T")[1];
        try{

            date = LocalDate.parse(dateString, dateFormatter);

            time = LocalTime.parse(timeString, timeFormatter);
            LocalDateTime exactDateTime = date.atTime(time);


            return exactDateTime.format(dateTimeFormatter);
        }
        catch(DateTimeParseException e){


            return "";
        }

    }
}

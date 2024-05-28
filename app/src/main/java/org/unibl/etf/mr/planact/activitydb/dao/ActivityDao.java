package org.unibl.etf.mr.planact.activitydb.dao;

import android.provider.ContactsContract;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.util.Constants;

import java.lang.annotation.Target;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import java.util.List;

@Dao
public interface ActivityDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY)
    List<Activity> getActivities();

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY +  " WHERE DATE(time) = :searchDate")
    List<Activity> getActivitiesWithDate(String searchDate);

    @Insert
    long insertActivity(Activity activity);

    @Delete
    void deleteActivity(Activity activity);

    @Update
    void updateActivity(Activity activity);

    @Delete
    void deleteActivities(Activity... activities);

    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " WHERE name LIKE '%' || :keyword || '%'")
    List<Activity> search(String keyword);

}

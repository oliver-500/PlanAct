package org.unibl.etf.mr.planact.activitydb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr.planact.activitydb.model.Image;
import org.unibl.etf.mr.planact.activitydb.model.Location;
import org.unibl.etf.mr.planact.util.Constants;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    long insertLocation(Location location);
    @Delete
    void deleteLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION  + " WHERE id_activity = :activityId")
    Location getLocationForActivity(long activityId);
}

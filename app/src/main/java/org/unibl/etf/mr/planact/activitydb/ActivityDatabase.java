package org.unibl.etf.mr.planact.activitydb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.unibl.etf.mr.planact.activitydb.converters.ActivityTypeConverter;
import org.unibl.etf.mr.planact.activitydb.converters.LocalDateConverter;
import org.unibl.etf.mr.planact.activitydb.converters.LocalDateTimeConverter;
import org.unibl.etf.mr.planact.activitydb.dao.ActivityDao;
import org.unibl.etf.mr.planact.activitydb.dao.ImageDao;
import org.unibl.etf.mr.planact.activitydb.dao.LocationDao;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.activitydb.model.Image;
import org.unibl.etf.mr.planact.activitydb.model.Location;
import org.unibl.etf.mr.planact.util.Constants;

@Database(entities = {Activity.class, Image.class, Location.class}, version = 1)
@TypeConverters({ActivityTypeConverter.class, LocalDateTimeConverter.class, LocalDateConverter.class})
public abstract class ActivityDatabase extends RoomDatabase {

    private static ActivityDatabase activityDB;

    public abstract ActivityDao getActivityDao();

    public abstract LocationDao getLocationDao();

    public abstract ImageDao getImageDao();

    public static synchronized ActivityDatabase getInstance(Context context){
        if(activityDB == null){
            activityDB = buildDatabaseInstance(context);
        }
        return activityDB;
    }

    private static ActivityDatabase buildDatabaseInstance(Context context){
        return Room.databaseBuilder(context, ActivityDatabase.class, Constants.DB_NAME).allowMainThreadQueries().build();
    }

    public void cleanup(){
        activityDB = null;
    }

}

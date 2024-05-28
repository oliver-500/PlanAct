package org.unibl.etf.mr.planact.activitydb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.activitydb.model.Image;
import org.unibl.etf.mr.planact.util.Constants;

import java.util.List;
@Dao
public interface ImageDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME_IMAGE  + " WHERE id_activity = :activityId")
    List<Image> getImagesForActivity(long activityId);

    @Query("DELETE FROM " + Constants.TABLE_NAME_IMAGE + " WHERE id_activity= :activityId")
    void deleteImagesForActivityId(long activityId);
    @Insert
    long insertImage(Image image);

    @Delete
    void deleteImage(Image image);

    @Update
    void updateImage(Image image);

    @Delete
    void deleteImages(Image... images);
}

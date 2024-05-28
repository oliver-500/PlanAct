package org.unibl.etf.mr.planact.activitydb.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.unibl.etf.mr.planact.util.Constants;

import java.nio.ByteBuffer;

@Entity(tableName = Constants.TABLE_NAME_IMAGE)
public class Image {
    public ByteBuffer getData() {
        return data;
    }

    public Image(ByteBuffer data, long id_activity, long width, long height) {
        this.data = data;
        this.height = height;
        this.width = width;
        this.id_activity = id_activity;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private ByteBuffer data;

    @PrimaryKey(autoGenerate = true)
    private long id_image;

    private long id_activity;
    private long height;
    private long width;

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getId_activity() {
        return id_activity;
    }

    public void setId_activity(long id_activity) {
        this.id_activity = id_activity;
    }

    public long getId_image() {
        return id_image;
    }

    public void setId_image(long id_image) {
        this.id_image = id_image;
    }
}

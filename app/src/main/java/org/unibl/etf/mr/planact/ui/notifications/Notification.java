package org.unibl.etf.mr.planact.ui.notifications;


import org.threeten.bp.LocalDateTime;

public class Notification {
    private String title;
    private LocalDateTime date;

    public Notification(String title, LocalDateTime date, int activity_id) {
        this.title = title;
        this.date = date;
        this.activity_id = activity_id;
    }

    public Notification() {
    }

    private int activity_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }
}

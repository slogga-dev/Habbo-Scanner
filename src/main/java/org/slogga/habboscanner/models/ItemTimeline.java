package org.slogga.habboscanner.models;

import java.sql.Date;

public class ItemTimeline {
    private final int id;
    private final Date date;
    private final String type;

    public ItemTimeline(int id, Date date, String type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
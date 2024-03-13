package org.slogga.habboscanner.models;

import lombok.Getter;

import java.sql.Date;

@Getter
public class ItemTimeline {
    private final int id;
    private final Date date;
    private final String type;

    public ItemTimeline(int id, Date date, String type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

}
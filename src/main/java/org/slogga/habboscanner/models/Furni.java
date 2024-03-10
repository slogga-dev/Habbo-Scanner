package org.slogga.habboscanner.models;

import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;

import java.sql.Timestamp;

public class Furni {
    private Integer id;

    private String classname;

    private String name;

    private String owner;

    private  FurnitypeEnum type;

    private int roomID;

    private String extraData;

    private Timestamp timestamp;

    public Furni(Integer id, String classname, String name, String owner,
                 FurnitypeEnum type, int roomID, String extradata, Timestamp timestamp) {
        this.id = id;
        this.classname = classname;
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.roomID = roomID;
        this.extraData = extradata;
        this.timestamp = timestamp;
    }
    public Furni(){}

    public Integer getId() {
        return id;
    }
    public String getClassname() {
        return classname;
    }


    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public FurnitypeEnum getType() {
        return type;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getExtraData() {
        return extraData;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setName(String name) {
        this.name = name;
    }
}

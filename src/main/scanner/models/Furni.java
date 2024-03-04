package scanner.models;

import scanner.models.furnitype.FurnitypeEnum;

import java.sql.Timestamp;

public class Furni {
    private int id;

    private String classname;

    private String name;

    private final String owner;

    private final FurnitypeEnum type;

    private final int roomID;

    private final String extraData;

    private final Timestamp timestamp;

    public Furni(int id, String classname, String name, String owner,
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

    public int getId() {
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

package org.slogga.habboscanner.models.furni;

import lombok.Getter;
import lombok.Setter;
import org.slogga.habboscanner.models.enums.FurnitypeEnum;

import java.sql.Timestamp;

@Getter
public class Furni {
    @Setter
    private Integer id;

    @Setter
    private String classname;

    @Setter
    private String name;

    private String owner;

    private  FurnitypeEnum type;

    private int roomId;

    private String extraData;

    private Timestamp timestamp;

    public Furni(Integer id, String classname, String name, String owner,
                 FurnitypeEnum type, int roomId, String extradata, Timestamp timestamp) {
        this.id = id;
        this.classname = classname;
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.roomId = roomId;
        this.extraData = extradata;
        this.timestamp = timestamp;
    }

    public Furni(){}
}

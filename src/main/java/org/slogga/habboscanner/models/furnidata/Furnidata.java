package org.slogga.habboscanner.models.furnidata;

import com.google.gson.Gson;

import org.slogga.habboscanner.models.furnitype.*;
import org.slogga.habboscanner.models.furnidata.itemtypes.*;

public class Furnidata {
    private static Furnidata instance = null;
    private final RoomItemTypes roomitemtypes;
    private final WallItemTypes wallitemtypes;

    public Furnidata(RoomItemTypes roomitemtypes, WallItemTypes wallitemtypes) {
        this.roomitemtypes = roomitemtypes;
        this.wallitemtypes = wallitemtypes;
    }

    public static void setInstance(String furnidataJSON){
        Gson gson = new Gson();

        instance = gson.fromJson(furnidataJSON, Furnidata.class);
    }

    public static Furnidata getInstance() {
        if (instance == null)
            throw new IllegalStateException("Furnidata instance has not yet been initialized.");

        return instance;
    }

    public Furnitype getFurnitype(int id, FurnitypeEnum type) {
        Furnitype floorFurnitype = roomitemtypes.getFurnitype()
                .stream()
                .filter(furnitype -> furnitype.getId() == id)
                .findAny()
                .orElse(null);

        Furnitype wallFurnitype = wallitemtypes.getFurnitype()
                .stream()
                .filter(furnitype -> furnitype.getId() == id)
                .findAny()
                .orElse(null);

        return type == FurnitypeEnum.FLOOR ? floorFurnitype : wallFurnitype;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        roomitemtypes.getFurnitype().forEach(furnitype -> stringBuilder.append(furnitype.toString()));
        wallitemtypes.getFurnitype().forEach(furnitype -> stringBuilder.append(furnitype.toString()));

        return stringBuilder.toString();
    }
}

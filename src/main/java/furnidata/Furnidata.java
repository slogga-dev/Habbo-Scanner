package furnidata;

import models.furnitype.Furnitype;
import models.furnitype.FurnitypeEnum;
import furnidata.itemtypes.RoomItemTypes;
import furnidata.itemtypes.WallItemTypes;

public class Furnidata {
    private static Furnidata instance = null;

    private final RoomItemTypes roomitemtypes;
    private final WallItemTypes wallitemtypes;

    public Furnidata(RoomItemTypes roomItemTypes, WallItemTypes wallItemTypes) {
        roomitemtypes = roomItemTypes;
        wallitemtypes = wallItemTypes;
    }

    public static void setInstance(Furnidata instance) {
        Furnidata.instance = instance;
    }

    public static Furnidata getInstance() {
        return Furnidata.instance;
    }

    public Furnitype getFurnitype(int id, FurnitypeEnum type) {
        Furnitype floorFurnitype = roomitemtypes.getFurnitype()
                .stream()
                .filter(furnitype -> furnitype.getID() == id)
                .findAny()
                .orElse(null);

        Furnitype wallFurnitype = wallitemtypes.getFurnitype()
                .stream()
                .filter(furnitype -> furnitype.getID() == id)
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

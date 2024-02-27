package furnidata.itemtypes;

import models.furnitype.Furnitype;

import java.util.ArrayList;

public abstract class ItemTypes {
    private final ArrayList<Furnitype> furnitype;

    protected ItemTypes(ArrayList<Furnitype> furnitype) {
        this.furnitype = furnitype;
    }

    public ArrayList<Furnitype> getFurnitype() {
        return furnitype;
    }
}

package org.slogga.habboscanner.models.furnidata.itemtypes;

import org.slogga.habboscanner.models.furnitype.Furnitype;

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

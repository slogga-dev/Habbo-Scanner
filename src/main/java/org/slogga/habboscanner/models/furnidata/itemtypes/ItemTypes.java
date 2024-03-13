package org.slogga.habboscanner.models.furnidata.itemtypes;

import lombok.Getter;
import org.slogga.habboscanner.models.furnitype.Furnitype;

import java.util.ArrayList;

@Getter
public abstract class ItemTypes {
    private final ArrayList<Furnitype> furnitype;

    protected ItemTypes(ArrayList<Furnitype> furnitype) {
        this.furnitype = furnitype;
    }

}

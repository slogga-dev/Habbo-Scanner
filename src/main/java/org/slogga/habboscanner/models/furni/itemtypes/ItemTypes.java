package org.slogga.habboscanner.models.furni.itemtypes;

import lombok.Getter;
import org.slogga.habboscanner.models.furni.Furnitype;

import java.util.ArrayList;

@Getter
public class ItemTypes {
    private final ArrayList<Furnitype> furnitype;

    protected ItemTypes(ArrayList<Furnitype> furnitype) {
        this.furnitype = furnitype;
    }
}

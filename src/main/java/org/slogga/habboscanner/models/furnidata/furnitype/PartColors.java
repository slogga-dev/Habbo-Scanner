package org.slogga.habboscanner.models.furnidata.furnitype;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class PartColors {
    private final ArrayList<String> color;

    public PartColors(ArrayList<String> color) {
        this.color = color;
    }
}

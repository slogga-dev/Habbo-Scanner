package org.slogga.habboscanner.models.furni;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class PartColors {
    private final ArrayList<String> color;

    public PartColors(ArrayList<String> color) {
        this.color = color;
    }
}

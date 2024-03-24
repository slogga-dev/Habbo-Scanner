package org.slogga.habboscanner.models.furni;

import lombok.Getter;
import org.slogga.habboscanner.models.furni.PartColors;

@Getter
public class Furnitype {
    private final int id;
    private final String classname;
    private final int revision;
    private final String category;
    private final int defaultdir;
    private final int xdim;
    private final int ydim;
    private final PartColors partcolors;
    private final String name;
    private final String description;
    private final Object adurl;
    private final int offerid;
    private final boolean buyout;
    private final int rentofferid;
    private final boolean rentbuyout;
    private final boolean bc;
    private final boolean excludeddynamic;
    private final String customparams;
    private final int specialtype;
    private final boolean canstandon;
    private final boolean cansiton;
    private final boolean canlayon;
    private final String furniline;
    private final Object environment;
    private final boolean rare;

    public Furnitype(int id, String classname, int revision, String category, int defaultdir,
            int xdim, int ydim, PartColors partcolors, String name, String description,
            Object adurl, int offerid, boolean buyout, int rentofferid, boolean rentbuyout,
            boolean bc, boolean excludeddynamic, String customparams, int specialtype,
            boolean canstandon, boolean cansiton, boolean canlayon, String furniline,
            Object environment, boolean rare) {
        this.id = id;
        this.classname = classname;
        this.revision = revision;
        this.category = category;
        this.defaultdir = defaultdir;
        this.xdim = xdim;
        this.ydim = ydim;
        this.partcolors = partcolors;
        this.name = name;
        this.description = description;
        this.adurl = adurl;
        this.offerid = offerid;
        this.buyout = buyout;
        this.rentofferid = rentofferid;
        this.rentbuyout = rentbuyout;
        this.bc = bc;
        this.excludeddynamic = excludeddynamic;
        this.customparams = customparams;
        this.specialtype = specialtype;
        this.canstandon = canstandon;
        this.cansiton = cansiton;
        this.canlayon = canlayon;
        this.furniline = furniline;
        this.environment = environment;
        this.rare = rare;
    }
}
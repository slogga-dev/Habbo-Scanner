package org.slogga.habboscanner.services;

import java.io.IOException;
import java.sql.*;

import java.util.Objects;

import gearth.extensions.parsers.IFurni;

import org.slogga.habboscanner.dao.mysql.data.*;
import org.slogga.habboscanner.models.enums.FurnitypeEnum;
import org.slogga.habboscanner.models.furni.Furnitype;

import org.slogga.habboscanner.models.furni.Furnidata;

import org.slogga.habboscanner.models.furni.Furni;

import org.slogga.habboscanner.HabboScanner;

public class FurniService {
    public static void insertFurni(IFurni furniMetadata, FurnitypeEnum type,
                                   int roomID, String extradata) {
        int id = furniMetadata.getId();
        int typeId = furniMetadata.getTypeId();
        String owner = furniMetadata.getOwnerName();

        Furnitype furnitype = Furnidata.getInstance().getFurnitype(typeId, type);

        String classname = furnitype.getClassname();
        String name = furnitype.getName() != null ? furnitype.getName() :
                (Objects.equals(classname, "poster") ? "Poster" : "");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Furni furni = new Furni(id, classname, name,
                owner, type, roomID, extradata, timestamp);

        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("room_furni_active.enabled"));

        try {
            DataModificationDAO.insertData(furni);

            if (!isRoomFurniActiveEnabled) return;

            DataActiveDAO.insertData(furni);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
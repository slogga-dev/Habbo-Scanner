package org.slogga.habboscanner.logic.game;

import java.util.*;
import java.util.stream.Collectors;

import gearth.extensions.parsers.*;

import lombok.Getter;

import org.slogga.habboscanner.models.enums.FurnitypeEnum;
import org.slogga.habboscanner.models.furni.Furnidata;

import org.slogga.habboscanner.models.furni.Furni;
import org.slogga.habboscanner.models.furni.Furnitype;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.services.FurniService;

import org.slogga.habboscanner.utils.UTF8Utils;

@Getter
public class ItemProcessor {
    private int highestSeenPieces;

    private final Furni oldestFurni;

    private String rarestFurniName;

    public ItemProcessor(){
        highestSeenPieces = Integer.MAX_VALUE;
        oldestFurni = new Furni();
    }

    public void processFloorItem(HFloorItem item, FurnitypeEnum type, int roomId) {
        if (item.getId() > 999999999) return;

        Furnitype furnitype = Furnidata.getInstance().getFurnitype(item.getTypeId(), type);

        Map<String, String> itemDefinition = HabboScanner.getInstance()
                .getFurnidataConfigurator().getItems().get(furnitype.getClassname());

        int seenPieces = Optional.ofNullable(itemDefinition)
                .map(map -> map.get("seen_pieces"))
                .map(Integer::parseInt)
                .orElse(0);

        if (highestSeenPieces > seenPieces) {
            highestSeenPieces = seenPieces;

            rarestFurniName = furnitype.getName();
        }

        if (oldestFurni.getId() == null || item.getId() < oldestFurni.getId()) {
            oldestFurni.setId(item.getId());
            oldestFurni.setClassname(furnitype.getClassname());
            oldestFurni.setName(furnitype.getName() != null ?
                    furnitype.getName() : (Objects.equals(oldestFurni.getClassname(),
                    "poster") ? "Poster" : ""));
        }

        Object[] rawExtradata = item.getStuff();
        String extradata = Arrays.stream(rawExtradata)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        extradata = UTF8Utils.convertToUTF8(extradata);

        FurniService.insertFurni(item, type, roomId, extradata);
    }

    public void processWallItem(HWallItem item, FurnitypeEnum type, int roomId) {
        String extradata = UTF8Utils.convertToUTF8(item.getState());

        FurniService.insertFurni(item, type, roomId, extradata);
    }

    public void processFurniAddition(int id, int typeId, int ownerId, String ownerName, String extradata) {
        IFurni furni = new IFurni() {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public int getTypeId() {
                return typeId;
            }

            @Override
            public int getUsagePolicy() {
                return 1;
            }

            @Override
            public int getOwnerId() {
                return ownerId;
            }

            @Override
            public String getOwnerName() {
                return ownerName;
            }
        };

        int roomId = HabboScanner.getInstance().getConfigurator().getRoomEntryHandler().getRoomId();
        FurnitypeEnum lastFurniPlacedType = HabboScanner.getInstance()
                .getConfigurator().getItemAdditionHandlers().getLastFurniPlacedType();

        FurniService.insertFurni(furni, lastFurniPlacedType, roomId, extradata);
    }
}

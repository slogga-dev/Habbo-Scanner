package org.slogga.habboscanner.handlers.item;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;

import gearth.extensions.parsers.*;

import gearth.protocol.HPacket;

import gearth.protocol.HMessage;

import lombok.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;

@Getter
@Data
public class ItemAdditionHandlers {
    private int lastFurniPlacedId;

    private FurnitypeEnum lastFurniPlacedType;

    public void onObjectAdd(HMessage message) {
        HPacket packet = message.getPacket();

        int id = packet.readInteger();

        if (id > 999999999)
            return;

        lastFurniPlacedId = id;
        lastFurniPlacedType = FurnitypeEnum.FLOOR;

        int typeId = packet.readInteger();

        int unknownVariable1 = packet.readInteger();
        int unknownVariable2 = packet.readInteger();
        int unknownVariable3 = packet.readInteger();
        String unknownVariable4 = packet.readString();
        String unknownVariable5 = packet.readString();
        int unknownVariable6 = packet.readInteger();

        int extradataCategory = packet.readInteger();
        String extradata = Arrays.toString(HStuff.readData(packet, extradataCategory));

        int unknownVariable7 = packet.readInteger();
        int unknownVariable8 = packet.readInteger();

        int ownerId = packet.readInteger();
        String ownerName = packet.readString();

        extradata = extradata.substring(1, extradata.length() - 1);

        byte[] bytes = extradata.getBytes(StandardCharsets.ISO_8859_1);
        extradata = new String(bytes, StandardCharsets.UTF_8);

        HabboScanner.getInstance().getConfigurator().getRoomEntryHandler().getItemProcessor()
                .processFurniAddition(id, typeId, ownerId, ownerName, extradata);
    }

    public void onItemAdd(HMessage message) {
        HPacket packet = message.getPacket();

        int id = Integer.parseInt(packet.readString());

        if (id > 999999999)
            return;

        lastFurniPlacedId = id;
        lastFurniPlacedType = FurnitypeEnum.WALL;

        int typeId = packet.readInteger();

        String location = packet.readString();

        String extradata = packet.readString();

        int unknownVariable1 = packet.readInteger();
        int unknownVariable2 = packet.readInteger();

        int ownerId = packet.readInteger();
        String ownerName = packet.readString();

        byte[] bytes = extradata.getBytes(StandardCharsets.ISO_8859_1);
        extradata = new String(bytes, StandardCharsets.UTF_8);

        HabboScanner.getInstance().getConfigurator().getRoomEntryHandler()
                .getItemProcessor().processFurniAddition(id, typeId, ownerId, ownerName, extradata);
    }
}

package org.slogga.habboscanner.models.entities;

import java.util.Arrays;
import java.util.stream.Collectors;

import gearth.extensions.parsers.HEntity;

import org.slogga.habboscanner.services.BotService;

import org.slogga.habboscanner.utils.UTF8Utils;

public class BotEntity extends BaseEntity {
    public BotEntity(HEntity entity, int roomId) {
        super(entity, roomId);
    }

    @Override
    public void processEntity() {
        Object[] rawExtradata = entity.getStuff();
        String extradata = Arrays.stream(rawExtradata)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        extradata = UTF8Utils.convertToUTF8(extradata);

        BotService.insertBot(entity, extradata, roomId);
    }
}
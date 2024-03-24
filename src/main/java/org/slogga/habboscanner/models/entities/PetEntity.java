package org.slogga.habboscanner.models.entities;

import gearth.extensions.parsers.HEntity;

import java.util.Arrays;
import java.util.stream.Collectors;

import java.io.IOException;
import java.sql.SQLException;

import org.slogga.habboscanner.dao.mysql.entities.PetsDAO;

import org.slogga.habboscanner.utils.UTF8Utils;

public class PetEntity extends BaseEntity {
    public PetEntity(HEntity entity, int roomId) {
        super(entity, roomId);
    }

    @Override
    public void processEntity() {
        Object[] rawExtradata = entity.getStuff();
        String extradata = Arrays.stream(rawExtradata)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        extradata = UTF8Utils.convertToUTF8(extradata);

        try {
            PetsDAO.insertPet(entity.getId(), entity.getName(), roomId, extradata);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
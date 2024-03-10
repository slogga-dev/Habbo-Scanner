package org.slogga.habboscanner.models.entities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.slogga.habboscanner.dao.mysql.entities.PetsDAO;
import gearth.extensions.parsers.HEntity;

import java.util.Arrays;
import java.util.stream.Collectors;

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

        extradata = new String(extradata.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        try {
            PetsDAO.insertPet(entity.getId(), entity.getName(), roomId, extradata);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
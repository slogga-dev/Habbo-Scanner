package org.slogga.habboscanner.logic.game;

import gearth.extensions.parsers.HEntity;
import org.slogga.habboscanner.models.entities.BaseEntity;
import org.slogga.habboscanner.models.entities.BotEntity;
import org.slogga.habboscanner.models.entities.HabboEntity;
import org.slogga.habboscanner.models.entities.PetEntity;

public class BaseEntityFactory {
    public static BaseEntity createEntity(HEntity entity, int roomId) {
        switch (entity.getEntityType()) {
            case HABBO:
                return new HabboEntity(entity, roomId);

            case BOT:
            case OLD_BOT:
                return new BotEntity(entity, roomId);

            case PET:
                return new PetEntity(entity, roomId);

            default:
                return null;
        }
    }
}

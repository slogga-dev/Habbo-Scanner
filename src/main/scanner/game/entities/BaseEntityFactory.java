package scanner.game.entities;

import gearth.extensions.parsers.HEntity;

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

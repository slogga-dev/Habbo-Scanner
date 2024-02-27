package utils;

import java.util.List;

import game.entities.*;

public class EntityFinder {
    public static HabboEntity findHabboBotEntity(List<BaseEntity> entities, String botName) {
        return entities.stream()
                .filter(entity -> entity instanceof HabboEntity && entity.isBotWithName(botName))
                .map(entity -> (HabboEntity) entity)
                .findFirst()
                .orElse(null);
    }
}

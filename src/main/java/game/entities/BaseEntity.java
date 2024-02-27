package game.entities;

import gearth.extensions.parsers.HEntity;

public abstract class BaseEntity {
    protected HEntity entity;
    protected int roomId;

    public BaseEntity(HEntity entity, int roomId) {
        this.entity = entity;
        this.roomId = roomId;
    }

    public abstract void processEntity();

    public int getIndex() {
        return entity.getIndex();
    }

    public boolean isBotWithName(String botName) {
        return entity.getName().equals(botName);
    }
}
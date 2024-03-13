package org.slogga.habboscanner.models.entities;

import gearth.extensions.parsers.HEntity;

public abstract class BaseEntity {
    protected HEntity entity;
    protected int roomId;

    public BaseEntity(HEntity entity, int roomId) {
        this.entity = entity;
        this.roomId = roomId;
    }

    public abstract void processEntity();

}
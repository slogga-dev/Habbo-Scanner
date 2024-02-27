package models.furnitype;

import java.util.Arrays;
import java.util.Objects;

public enum FurnitypeEnum {
    FLOOR("Floor"),
    WALL("Wall");

    public static FurnitypeEnum fromValue(String value) {
        return Arrays.stream(FurnitypeEnum.values())
                .filter(mode -> Objects.equals(mode.getType(), value))
                .findFirst()
                .orElse(null);
    }

    private final String type;

    FurnitypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

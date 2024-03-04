package scanner.game.entities;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import gearth.extensions.parsers.HEntity;

import scanner.services.BotService;

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

        extradata = new String(extradata.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        BotService.insertBot(entity, extradata, roomId);
    }
}
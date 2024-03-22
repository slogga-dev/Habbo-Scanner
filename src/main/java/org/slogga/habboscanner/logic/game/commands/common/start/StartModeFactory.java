package org.slogga.habboscanner.logic.game.commands.common.start;

import java.util.*;
import java.util.function.Supplier;

import org.slogga.habboscanner.logic.game.commands.common.start.modes.*;

public class StartModeFactory {
    private static final Map<String, Supplier<IStarter>> startModeMap = new HashMap<>();

    static {
        startModeMap.put("bot.per.id", StartBotPerId::new);
        startModeMap.put("bot.per.owner.name.list", StartBotPerOwnerNameList::new);
        startModeMap.put("bot.per.room.id.list", StartBotPerRoomIdList::new);
        startModeMap.put("bot.in.active.rooms", StartBotInActiveRooms::new);
    }

    public static IStarter getStartModeStrategy(String modeKey) {
        Supplier<IStarter> modeSupplier = startModeMap.get(modeKey);

        if (modeSupplier == null)
            throw new IllegalArgumentException("Invalid start mode key");

        return modeSupplier.get();
    }

    public static Set<String> getStartModeKeys() {
        return startModeMap.keySet();
    }
}

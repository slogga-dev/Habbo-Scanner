package org.slogga.habboscanner.logic.game.commands.common.follow;

import java.util.*;
import java.util.function.Supplier;

import org.slogga.habboscanner.logic.game.commands.common.follow.actions.*;
import org.slogga.habboscanner.models.FollowingAction;

public class FollowingActionModeFactory {
    private static final Map<FollowingAction, Supplier<IFollower>> actionModeMap = new HashMap<>();

    static {
        actionModeMap.put(FollowingAction.FURNI_INFO, FurniInfoFollowingAction::new);
        actionModeMap.put(FollowingAction.AUCTION, AuctionFollowingAction::new);
    }

    public static IFollower getFollowingActionStrategy(FollowingAction action) {
        Supplier<IFollower> modeSupplier = actionModeMap.get(action);

        if (modeSupplier == null)
            throw new IllegalArgumentException("Invalid follow mode key");

        return modeSupplier.get();
    }
}

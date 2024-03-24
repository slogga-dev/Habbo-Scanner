package org.slogga.habboscanner.logic.game.commands.common.follow;

import java.util.*;
import java.util.function.Supplier;

import org.slogga.habboscanner.logic.game.commands.common.follow.actions.*;
import org.slogga.habboscanner.models.FollowingAction;

public class FollowingActionModeFactory {
    private static final Map<FollowingAction, Supplier<BaseFollowingAction>> actionModeMap = new HashMap<>();

    static {
        actionModeMap.put(FollowingAction.DEFAULT, DefaultFollowingAction::new);
        actionModeMap.put(FollowingAction.FURNI_INFO, FurniInfoFollowingAction::new);
        actionModeMap.put(FollowingAction.AUCTION, AuctionFollowingAction::new);
    }

    public static BaseFollowingAction getFollowingActionStrategy(FollowingAction action) {
        Supplier<BaseFollowingAction> modeSupplier = actionModeMap.get(action);

        if (modeSupplier == null) {
            Supplier<BaseFollowingAction> defaultActionMode = actionModeMap.get(FollowingAction.DEFAULT);

            return defaultActionMode.get();
        }

        return modeSupplier.get();
    }
}

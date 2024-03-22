package org.slogga.habboscanner.logic.game.commands.common.follow;

import java.util.*;

import org.slogga.habboscanner.logic.game.commands.common.follow.actions.*;
import org.slogga.habboscanner.models.FollowingAction;

public class FollowingActionModeFactory {
    private static final Map<FollowingAction, IFollower> actionModes = new HashMap<>();

    static {
        actionModes.put(FollowingAction.FURNI_INFO, FurniInfoFollowingAction::new);
        actionModes.put(FollowingAction.AUCTION, AuctionFollowingAction::new);
    }

    public static IFollower getFollowingActionStrategy(FollowingAction action) {
        return actionModes.get(action);
    }
}
package org.slogga.habboscanner.models;

import java.util.*;

public enum FollowingAction {
    FURNI_INFO("furni_info"),
    AUCTION("auction");

    public static FollowingAction fromValue(String value) {
        return Arrays.stream(FollowingAction.values())
                .filter(mode -> Objects.equals(mode.getAction(), value))
                .findFirst()
                .orElse(null);
    }

    private final String action;

    FollowingAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

package org.slogga.habboscanner.logic.commands.common.follow;

import gearth.protocol.HMessage;

// It's a strategy
public interface IFollower {
    void execute(HMessage message);
}

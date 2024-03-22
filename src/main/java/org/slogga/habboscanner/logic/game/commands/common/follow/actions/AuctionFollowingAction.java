package org.slogga.habboscanner.logic.game.commands.common.follow.actions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.common.follow.IFollower;

import org.slogga.habboscanner.HabboScanner;

public class AuctionFollowingAction implements IFollower {
    @Override
    public void execute() {
        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        String followingAuctionMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("following.auction.message");

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        HabboActions.sendPrivateMessage(consoleUserId, followingAuctionMessage);

        AtomicReference<Instant> lastMessageTime = new AtomicReference<>(Instant.now());

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            long differenceInMinutes = ChronoUnit.MINUTES.between(lastMessageTime.get(), Instant.now());

            if (differenceInMinutes <= 13)
                return;

            // Wave for anti AFK.
            HabboActions.sendAvatarExpression(1);

            lastMessageTime.set(Instant.now());
        }, 0, 1, TimeUnit.SECONDS);
    }
}

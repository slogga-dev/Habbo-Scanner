package org.slogga.habboscanner.logic.game.console.commands.follow.actions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.commands.follow.FollowingActionMode;

import org.slogga.habboscanner.HabboScanner;

public class AuctionFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        String followingAuctionMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("following.auction.message");
        scheduledExecutorService.schedule(() -> HabboActions.sendPrivateMessage(consoleUserId, followingAuctionMessage),
                1000, TimeUnit.MILLISECONDS);

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

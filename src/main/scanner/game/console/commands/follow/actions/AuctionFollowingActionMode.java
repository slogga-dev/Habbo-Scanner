package scanner.game.console.commands.follow.actions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import scanner.game.console.commands.follow.FollowingActionMode;

import scanner.HabboScanner;

public class AuctionFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        scheduledExecutorService.schedule(() -> HabboScanner.getInstance().sendPrivateMessage(
                consoleUserId, "boo ora seguo questa asta, promette bene"),
                1000, TimeUnit.MILLISECONDS);

        AtomicReference<Instant> lastMessageTime = new AtomicReference<>(Instant.now());

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            long differenceInMinutes = ChronoUnit.MINUTES.between(lastMessageTime.get(), Instant.now());

            if (differenceInMinutes <= 13)
                return;

            // Wave for anti AFK.
            HabboScanner.getInstance().sendAvatarExpression(1);

            lastMessageTime.set(Instant.now());
        }, 0, 1, TimeUnit.SECONDS);
    }
}

package org.slogga.habboscanner.logic.game.commands.Console.commands.start.modes;

import lombok.Setter;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartMode;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.concurrent.*;

@Setter
public class StartBotInActiveRoomsMode implements StartMode {
    private boolean isProcessingActiveRooms;

    @Override
    public void handle(int userId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands()
                    .get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning || isProcessingActiveRooms)
                return;

            HabboActions.sendNavigatorSearch("groups", "");

            isProcessingActiveRooms = true;
        }, 0, 2, TimeUnit.SECONDS);
    }

    public boolean getIsProcessingActiveRooms() {
        return isProcessingActiveRooms;
    }

}
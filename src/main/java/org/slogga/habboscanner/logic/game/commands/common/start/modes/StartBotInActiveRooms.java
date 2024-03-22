package org.slogga.habboscanner.logic.game.commands.common.start.modes;

import lombok.Setter;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.common.start.IStarter;

import org.slogga.habboscanner.logic.game.commands.common.start.StartCommand;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.concurrent.*;

@Setter
public class StartBotInActiveRooms implements IStarter {
    private boolean isProcessingActiveRooms;

    @Override
    public void execute(CommandExecutorProperties properties) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance.getCommands()
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
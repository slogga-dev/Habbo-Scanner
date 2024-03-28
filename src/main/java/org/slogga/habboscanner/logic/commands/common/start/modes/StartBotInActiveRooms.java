package org.slogga.habboscanner.logic.commands.common.start.modes;

import java.util.concurrent.*;

import lombok.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.commands.*;
import org.slogga.habboscanner.logic.commands.common.start.IStarter;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;

import org.slogga.habboscanner.models.enums.CommandKeys;

@Getter
@Setter
public class StartBotInActiveRooms implements IStarter {

    @Override
    public void execute(CommandExecutorProperties properties) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance.getCommands()
                    .get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning) return;

            HabboActions.sendNavigatorSearch("groups", "");
        }, 0, 2, TimeUnit.SECONDS);
    }
}
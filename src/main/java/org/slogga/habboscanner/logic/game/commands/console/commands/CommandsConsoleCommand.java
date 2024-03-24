package org.slogga.habboscanner.logic.game.commands.console.commands;

import java.util.*;
import java.util.concurrent.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.Command;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;

import org.slogga.habboscanner.HabboScanner;

public class CommandsConsoleCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        Map<String, Command> commands = CommandFactory.commandExecutorInstance.getCommands();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        int delay = 0;

        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            String name = entry.getKey();
            Command command = entry.getValue();
            String description = command.getDescription();

            String commandMessageText = name + " - " + description;

            executor.schedule(() -> HabboActions.sendPrivateMessage(properties.getUserId(), commandMessageText), delay, TimeUnit.SECONDS);

            delay++;
        }

        executor.schedule(() -> HabboActions.sendPrivateMessage(properties.getUserId(), "----------------------"), delay, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("commands.commands.command.description");
    }
}

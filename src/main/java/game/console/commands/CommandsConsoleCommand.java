package game.console.commands;

import java.util.*;
import java.util.concurrent.*;

import game.console.ConsoleCommand;

import gearth.protocol.HMessage;

import scanner.HabboScanner;

public class CommandsConsoleCommand implements ConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        Map<String, ConsoleCommand> commands = HabboScanner.getInstance().getConsoleHandlers().getCommands();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        int delay = 0;

        for (Map.Entry<String, ConsoleCommand> entry : commands.entrySet()) {
            String name = entry.getKey();
            ConsoleCommand command = entry.getValue();
            String description = command.getDescription();

            String commandMessageText = name + " - " + description;

            executor.schedule(() -> HabboScanner.getInstance()
                    .sendPrivateMessage(userId, commandMessageText), delay, TimeUnit.SECONDS);

            delay++;
        }

        executor.schedule(() -> HabboScanner.getInstance()
                .sendPrivateMessage(userId, "----------------------"), delay, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.commands.command.description");
    }
}

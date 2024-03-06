package scanner.game.console.commands;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang3.NotImplementedException;
import scanner.game.console.IConsoleCommand;

import gearth.protocol.HMessage;

import scanner.HabboScanner;

public class CommandsConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        Map<String, IConsoleCommand> commands = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getCommands();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        int delay = 0;

        for (Map.Entry<String, IConsoleCommand> entry : commands.entrySet()) {
            String name = entry.getKey();
            IConsoleCommand command = entry.getValue();
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
    public void resetForStart() {
        throw new NotImplementedException();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.commands.command.description");
    }
}

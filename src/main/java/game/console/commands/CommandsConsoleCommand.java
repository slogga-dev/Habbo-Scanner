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
        Set<String> adminOnlyCommands = HabboScanner.getInstance().getConsoleHandlers().getAdminOnlyCommands();

        String allowedAdminUserIds = HabboScanner.getInstance()
                .getBotProperties().getProperty("admin.chat.commands.allowed.user.ids");
        List<String> allowedAdminUserIdsList = Arrays.asList(allowedAdminUserIds.split(" "));
        boolean allowedAdminUser = allowedAdminUserIdsList.contains(String.valueOf(userId));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        int delay = 0;

        for (Map.Entry<String, ConsoleCommand> entry : commands.entrySet()) {
            String name = entry.getKey();
            ConsoleCommand command = entry.getValue();
            String description = command.getDescription();

            if (!allowedAdminUser && adminOnlyCommands.contains(name)) continue;

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

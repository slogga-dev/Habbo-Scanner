package handlers;

import java.util.*;

import game.console.commands.convert.ConvertConsoleCommand;
import game.console.commands.follow.FollowConsoleCommand;
import gearth.protocol.HMessage;

import game.console.ConsoleCommand;
import game.console.commands.*;
import game.console.commands.start.StartConsoleCommand;

import scanner.HabboScanner;

public class ConsoleHandlers {
    private final Map<String, ConsoleCommand> commands = new HashMap<>();
    private final Set<String> adminOnlyCommands = new HashSet<>(Arrays.asList(":start", ":pause",
            ":resume", ":convert", ":logout", ":info", ":update", ":energy_saving"));

    private int userId;

    public ConsoleHandlers() {
        commands.put(":start", new StartConsoleCommand());
        commands.put(":pause", new PauseConsoleCommand());
        commands.put(":resume", new ResumeConsoleCommand());
        commands.put(":follow", new FollowConsoleCommand());
        commands.put(":info", new InfoConsoleCommand());
        commands.put(":convert", new ConvertConsoleCommand());
        commands.put(":update", new UpdateConsoleCommand());
        commands.put(":makesay", new MakeSayCommand());
        commands.put(":logout", new LogoutConsoleCommand());
        commands.put(":energy_saving", new EnergySavingConsoleCommand());
        commands.put(":commands", new CommandsConsoleCommand());
    }

    public void onNewConsole(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getBotProperties().getProperty("bot.enabled"));

        if (!isBotEnabled) return;

        userId = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        String allowedUserIds = HabboScanner.getInstance().getBotProperties().getProperty("chat.commands.allowed.user.ids");
        List<String> allowedUserIdsList = Arrays.asList(allowedUserIds.split(" "));
        boolean allowedUser = allowedUserIdsList.contains(String.valueOf(userId));

        String allowedAdminUserIds = HabboScanner.getInstance().getBotProperties().getProperty("admin.chat.commands.allowed.user.ids");
        List<String> allowedAdminUserIdsList = Arrays.asList(allowedAdminUserIds.split(" "));
        boolean allowedAdminUser = allowedAdminUserIdsList.contains(String.valueOf(userId));

        for (Map.Entry<String, ConsoleCommand> entry : commands.entrySet()) {
            if (!messageText.startsWith(entry.getKey())) continue;

            if (allowedAdminUser) {
                entry.getValue().execute(message, messageText, userId);

                continue;
            }

            if (!allowedUser || adminOnlyCommands.contains(entry.getKey())) continue;

            entry.getValue().execute(message, messageText, userId);
        }
    }

    public Map<String, ConsoleCommand> getCommands() {
        return commands;
    }

    public Set<String> getAdminOnlyCommands() {
        return adminOnlyCommands;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
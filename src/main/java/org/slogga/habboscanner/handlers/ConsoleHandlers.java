package org.slogga.habboscanner.handlers;

import java.util.*;

import gearth.protocol.HMessage;

import lombok.Getter;
import lombok.Setter;
import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.game.console.commands.convert.ConvertConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.follow.FollowConsoleCommand;

import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.*;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.HabboScanner;

@Getter
public class ConsoleHandlers {
    private final Map<String, IConsoleCommand> commands = new HashMap<>();

    @Setter
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
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator().getProperties().get("bot").getProperty("bot.enabled"));

        if (!isBotEnabled) return;

        userId = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        for (Map.Entry<String, IConsoleCommand> entry : commands.entrySet()) {
            if (!messageText.startsWith(entry.getKey())) continue;

            HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().setItemProcessor(new ItemProcessor());
            entry.getValue().execute(message, messageText, userId);
        }
    }
}
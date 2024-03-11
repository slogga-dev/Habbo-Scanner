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
import org.slogga.habboscanner.logic.game.console.commands.follow.actions.FurniInfoFollowingActionMode;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;
import org.slogga.habboscanner.models.FollowingAction;

@Getter
public class ConsoleHandlers {
    private final Map<String, IConsoleCommand> commands = new HashMap<>();

    @Setter
    private int userId;

    public ConsoleHandlers() {
        commands.put(CommandKeys.START.getKey(), new StartConsoleCommand());
        commands.put(CommandKeys.PAUSE.getKey(), new PauseConsoleCommand());
        commands.put(CommandKeys.RESUME.getKey(), new ResumeConsoleCommand());
        commands.put(CommandKeys.FOLLOW.getKey(), new FollowConsoleCommand());
        commands.put(CommandKeys.INFO.getKey(), new InfoConsoleCommand());
        commands.put(CommandKeys.CONVERT.getKey(), new ConvertConsoleCommand());
        commands.put(CommandKeys.UPDATE.getKey(), new UpdateConsoleCommand());
        commands.put(CommandKeys.MAKESAY.getKey(), new MakeSayCommand());
        commands.put(CommandKeys.LOGOUT.getKey(), new LogoutConsoleCommand());
        commands.put(CommandKeys.ENERGY_SAVING.getKey(), new EnergySavingConsoleCommand());
        commands.put(CommandKeys.COMMANDS.getKey(), new CommandsConsoleCommand());
    }

    public void onNewConsole(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator().getProperties().get("bot").getProperty("bot.enabled"));

        if (!isBotEnabled) return;

        userId = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) commands.get(CommandKeys.FOLLOW.getKey());
        FurniInfoFollowingActionMode furniInfoFollowingActionMode = (FurniInfoFollowingActionMode)
                followConsoleCommand.getActionModes().get(FollowingAction.FURNI_INFO);

        if (messageText.equals("go away")) furniInfoFollowingActionMode.goAway();

        for (Map.Entry<String, IConsoleCommand> entry : commands.entrySet()) {
            if (!messageText.startsWith(entry.getKey())) continue;

            HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().setItemProcessor(new ItemProcessor());

            entry.getValue().execute(message, messageText, userId);
        }
    }
}
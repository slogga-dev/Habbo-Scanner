package org.slogga.habboscanner.logic.game.commands.Console.commands;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;

public class MakeSayCommand implements IExecuteCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        String[] arguments = properties.getMessageText().split(" ", 2);

        if (arguments.length < 2) {
            String missingMessageWarning = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                    .getProperty("missing.message.warning");

            HabboActions.sendPrivateMessage(properties.getUserId(), missingMessageWarning);

            return;
        }

        String consoleMessageText = arguments[1].trim();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) {
            HabboActions.sendPrivateMessage(properties.getUserId(), consoleMessageText);

            return;
        }

        discordBot.sendMessageToFeedChannel(consoleMessageText);

        String messageSentConfirmation = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("message.sent.confirmation");

        HabboActions.sendPrivateMessage(properties.getUserId(), messageSentConfirmation);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.makesay.command.description");
    }
}

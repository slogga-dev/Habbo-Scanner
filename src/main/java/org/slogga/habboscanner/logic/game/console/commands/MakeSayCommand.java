package org.slogga.habboscanner.logic.game.console.commands;

import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.discord.DiscordBot;
import gearth.protocol.HMessage;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;

import org.slogga.habboscanner.HabboScanner;

public class MakeSayCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String[] arguments = messageText.split(" ", 2);

        if (arguments.length < 2) {
            String missingMessageWarning = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                    .getProperty("missing.message.warning");

            HabboActions.sendPrivateMessage(userId, missingMessageWarning);

            return;
        }

        String consoleMessageText = arguments[1].trim();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) {
            HabboActions.sendPrivateMessage(userId, consoleMessageText);

            return;
        }

        discordBot.sendMessageToFeedChannel(consoleMessageText);

        String messageSentConfirmation = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("message.sent.confirmation");

        HabboActions.sendPrivateMessage(userId, messageSentConfirmation);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.makesay.command.description");
    }
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
}

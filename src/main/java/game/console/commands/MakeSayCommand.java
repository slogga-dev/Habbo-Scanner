package game.console.commands;

import discord.DiscordBot;
import gearth.protocol.HMessage;

import game.console.ConsoleCommand;

import scanner.HabboScanner;

public class MakeSayCommand implements ConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String[] arguments = messageText.split(" ", 2);

        if (arguments.length < 2) {
            String missingMessageWarning = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("missing.message.warning");

            HabboScanner.getInstance().sendPrivateMessage(userId, missingMessageWarning);

            return;
        }

        String discordMessageText = arguments[1].trim();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        discordBot.sendMessageToFeedChannel(discordMessageText);

        String messageSentConfirmation = HabboScanner.getInstance()
                .getMessageProperties().getProperty("message.sent.confirmation");

        HabboScanner.getInstance().sendPrivateMessage(userId, messageSentConfirmation);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.makesay.command.description");
    }
}

package scanner.game.console.commands;

import org.apache.commons.lang3.NotImplementedException;
import scanner.discord.DiscordBot;
import gearth.protocol.HMessage;

import scanner.game.console.IConsoleCommand;

import scanner.HabboScanner;

public class MakeSayCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String[] arguments = messageText.split(" ", 2);

        if (arguments.length < 2) {
            String missingMessageWarning = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                    .getProperty("missing.message.warning");

            HabboScanner.getInstance().sendPrivateMessage(userId, missingMessageWarning);

            return;
        }

        String consoleMessageText = arguments[1].trim();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) {
            HabboScanner.getInstance().sendPrivateMessage(userId, consoleMessageText);

            return;
        }

        discordBot.sendMessageToFeedChannel(consoleMessageText);

        String messageSentConfirmation = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("message.sent.confirmation");

        HabboScanner.getInstance().sendPrivateMessage(userId, messageSentConfirmation);
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

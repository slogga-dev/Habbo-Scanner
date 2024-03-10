package org.slogga.habboscanner.discord.commands;

import org.slogga.habboscanner.discord.DiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.HabboActions;

import java.util.Properties;

public class MakeSayCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping textOption = event.getOption("text");

        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message");

        if (textOption == null) {
            String missingMessageWarning = messageProperties.getProperty("missing.message.warning");

            event.reply(missingMessageWarning).queue();

            return;
        }

        String discordUserId = event.getUser().getId();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        HabboActions.sendPrivateMessage(habboUserId, textOption.getAsString());

        String habboMessageSentConfirmation = messageProperties.getProperty("habbo.message.sent.confirmation");

        event.reply(habboMessageSentConfirmation).queue();
    }
}
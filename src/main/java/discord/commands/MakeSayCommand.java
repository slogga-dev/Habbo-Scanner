package discord.commands;

import discord.DiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import discord.DiscordCommand;

import scanner.HabboScanner;

import java.util.Properties;

public class MakeSayCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping textOption = event.getOption("text");

        Properties messageProperties = HabboScanner.getInstance().getMessageProperties();

        if (textOption == null) {
            String missingMessageWarning = messageProperties.getProperty("missing.message.warning");

            event.reply(missingMessageWarning).queue();

            return;
        }

        String discordUserId = event.getUser().getId();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        HabboScanner.getInstance().sendPrivateMessage(habboUserId, textOption.getAsString());

        String habboMessageSentConfirmation = messageProperties.getProperty("habbo.message.sent.confirmation");

        event.reply(habboMessageSentConfirmation).queue();
    }
}
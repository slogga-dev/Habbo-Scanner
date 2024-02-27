package discord.commands;

import java.util.Objects;
import java.util.Properties;

import discord.DiscordBot;

import game.console.commands.convert.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import scanner.HabboScanner;

public class ConvertDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String file = Objects.requireNonNull(event.getOption("file")).getAsString();

        Properties messageProperties = HabboScanner.getInstance().getMessageProperties();

        if (!file.equals("items") && !file.equals("timeline")) {
            String invalidFileReply = messageProperties.getProperty("invalid.file.message");

            event.reply(invalidFileReply).setEphemeral(true).queue();

            return;
        }

        ConvertConsoleCommand convertConsoleCommand = (ConvertConsoleCommand)
                HabboScanner.getInstance().getConsoleHandlers()
                .getCommands().get(":convert");

        ConvertFile convertFile = convertConsoleCommand.getConvertFiles().get(file);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        String discordUserId = event.getUser().getId();
        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        convertFile.handle(habboUserId);

        String startConversionReply = messageProperties.getProperty("bot.start.conversion.message");

        event.reply(startConversionReply).queue();
    }
}
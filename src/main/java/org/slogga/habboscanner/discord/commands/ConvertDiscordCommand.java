package org.slogga.habboscanner.discord.commands;

import java.util.Objects;
import java.util.Properties;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.logic.game.console.commands.convert.*;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.DiscordCommand;

import org.slogga.habboscanner.HabboScanner;

public class ConvertDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String file = Objects.requireNonNull(event.getOption("file")).getAsString();

        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties().get("message");

        if (!file.equals("items") && !file.equals("timeline")) {
            String invalidFileMessage = messageProperties.getProperty("invalid.file.message");

            event.reply(invalidFileMessage).setEphemeral(true).queue();

            return;
        }

        ConvertConsoleCommand convertConsoleCommand = (ConvertConsoleCommand)
                HabboScanner.getInstance().getConfigurator().getConsoleHandlers()
                .getCommands().get(":convert");

        ConvertFile convertFile = convertConsoleCommand.getConvertFiles().get(file);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        String discordUserId = event.getUser().getId();
        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        convertFile.handle(habboUserId);

        String startConversionMessage = messageProperties.getProperty("bot.start.conversion.message");

        event.reply(startConversionMessage).queue();
    }
}
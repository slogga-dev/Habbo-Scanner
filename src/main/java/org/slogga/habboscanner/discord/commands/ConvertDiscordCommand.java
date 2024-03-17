package org.slogga.habboscanner.discord.commands;

import java.util.Objects;
import java.util.Properties;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.convert.ConvertConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.convert.ConvertFile;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

public class ConvertDiscordCommand implements IDiscordCommand {
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

        ConvertConsoleCommand convertConsoleCommand = (ConvertConsoleCommand)CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.CONVERT.getKey());

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
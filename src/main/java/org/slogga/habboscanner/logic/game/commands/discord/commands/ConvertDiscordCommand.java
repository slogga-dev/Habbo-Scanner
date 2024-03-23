package org.slogga.habboscanner.logic.game.commands.discord.commands;

import java.util.Objects;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.common.convert.ConvertCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.ConvertFile;

public class ConvertDiscordCommand extends ConvertCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        SlashCommandInteractionEvent event = properties.getEvent();

        String file = Objects.requireNonNull(event.getOption("file")).getAsString();

        if (!file.equals("items") && !file.equals("timeline")) {
            String invalidFileMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties().get("message")
                    .getProperty("invalid.file.message");

            event.reply(invalidFileMessage).setEphemeral(true).queue();

            return;
        }

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        String discordUserId = event.getUser().getId();
        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        properties.setUserId(habboUserId);

        convertFile = ConvertFile.fromValue(file);

        super.execute(properties);
    }
}
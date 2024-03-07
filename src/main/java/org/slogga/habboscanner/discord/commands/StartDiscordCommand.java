package org.slogga.habboscanner.discord.commands;

import java.util.*;

import java.util.stream.Collectors;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.logic.game.console.commands.start.*;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.HabboScanner;

public class StartDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers().getCommands().get(":start");

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        if (startConsoleCommand.getIsBotRunning()) {
            String botAlreadySearchingMessage = messageProperties.getProperty("bot.already.searching.message");

            event.reply(botAlreadySearchingMessage).queue();

            return;
        }

        startConsoleCommand.setIsBotRunning(true);

        List<Map.Entry<String, StartMode>> enabledModes = startConsoleCommand.getStartModes().entrySet().stream()
                .filter(entry -> Boolean.parseBoolean(HabboScanner.getInstance()
                        .getConfigurator().getProperties().get("bot").getProperty(entry.getKey())))
                .collect(Collectors.toList());

        if (enabledModes.size() != 1) {
            String impossibleStartBotMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message").getProperty("impossible.start.bot.message");

            event.reply(impossibleStartBotMessage).queue();

            return;
        }

        StartMode enabledMode = enabledModes.get(0).getValue();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        String discordUserId = event.getUser().getId();

        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        enabledMode.handle(habboUserId);

        String botStartSearchingMessage = messageProperties.getProperty("bot.start.searching.message");

        event.reply(botStartSearchingMessage).queue();
    }
}
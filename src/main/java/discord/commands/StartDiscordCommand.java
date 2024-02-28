package discord.commands;

import java.util.*;

import java.util.stream.Collectors;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import game.console.commands.start.*;

import discord.DiscordBot;

import scanner.HabboScanner;

public class StartDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":start");

        Properties messageProperties = HabboScanner.getInstance().getMessageProperties();

        if (startConsoleCommand.getIsBotRunning()) {
            String botAlreadySearchingMessage = messageProperties.getProperty("bot.already.searching.message");

            event.reply(botAlreadySearchingMessage).queue();

            return;
        }

        startConsoleCommand.setIsBotRunning(true);

        List<Map.Entry<String, StartMode>> enabledModes = startConsoleCommand.getStartModes().entrySet().stream()
                .filter(entry -> Boolean.parseBoolean(HabboScanner.getInstance()
                        .getBotProperties().getProperty(entry.getKey())))
                .collect(Collectors.toList());

        if (enabledModes.size() != 1) {
            String impossibleStartBotMessage = HabboScanner.getInstance().getMessageProperties().getProperty("impossible.start.bot.message");

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
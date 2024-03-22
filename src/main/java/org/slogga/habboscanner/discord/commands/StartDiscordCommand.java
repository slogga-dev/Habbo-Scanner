package org.slogga.habboscanner.discord.commands;

import java.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.common.start.IStarter;
import org.slogga.habboscanner.logic.game.commands.Command;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.common.start.StartModeFactory;
import org.slogga.habboscanner.models.CommandKeys;

public class StartDiscordCommand extends Command{
    @Override
    public void execute(CommandExecutorProperties properties) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
        SlashCommandInteractionEvent event = properties.getEvent();
        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        if (startConsoleCommand.isBotRunning()) {
            String botAlreadySearchingMessage = messageProperties.getProperty("bot.already.searching.message");

            event.reply(botAlreadySearchingMessage).queue();

            return;
        }

        startConsoleCommand.setBotRunning(true);

        String enabledModeKey = getEnableModeKey(properties);

        if (enabledModeKey == null)
            return;

        IStarter starter = StartModeFactory.getStartModeStrategy(enabledModeKey);

        CompletableFuture.runAsync(() -> starter.execute(properties));

        String botStartSearchingMessage = messageProperties.getProperty("bot.start.searching.message");

        event.reply(botStartSearchingMessage).queue();
    }

    private String getEnableModeKey(CommandExecutorProperties properties) {
        List<String> enabledModeKeys = StartModeFactory.getStartModeKeys().stream()
                .filter(modeKey -> Boolean.parseBoolean(HabboScanner.getInstance()
                        .getConfigurator()
                        .getProperties()
                        .get("bot")
                        .getProperty(modeKey)))
                .collect(Collectors.toList());

        if (enabledModeKeys.size() != 1) {
            String impossibleStartBotMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("message")
                    .getProperty("impossible.start.bot.message");

            sendMessage(impossibleStartBotMessage, properties);

            return null;
        }

        return enabledModeKeys.get(0);
    }
    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.start.command.description");
    }
}
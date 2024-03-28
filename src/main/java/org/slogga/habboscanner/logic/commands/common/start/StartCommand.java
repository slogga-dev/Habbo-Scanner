package org.slogga.habboscanner.logic.commands.common.start;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.commands.*;
import org.slogga.habboscanner.logic.commands.common.EnergySavingCommand;
import org.slogga.habboscanner.models.enums.CommandKeys;

@EqualsAndHashCode(callSuper = true)
@Data
public class StartCommand extends Command {
    private boolean hasExecuted = false;

    private boolean isBotRunning = false;

    @Override
    public void execute(CommandExecutorProperties properties) {
        if (hasExecuted) return;

        hasExecuted = true;
        isBotRunning = true;

        HabboScanner.getInstance().getConfigurator().getRoomEntryHandler().refreshLastRoomAccess();

        boolean isEnergySavingModeEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator().getProperties().get("bot").getProperty("bot.energy.saving.mode.enabled"));

        EnergySavingCommand energySavingCommand = (EnergySavingCommand)
                CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.ENERGY_SAVING.getKey());

        if (isEnergySavingModeEnabled) energySavingCommand.setEnergySavingMode(true);

        String enabledModeKey = getEnableModeKey(properties);

        if (enabledModeKey == null) return;

        IStarter starter = StartModeFactory.getStartModeStrategy(enabledModeKey);

        CompletableFuture.runAsync(() -> starter.execute(properties));
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("start.command.description");
    }

    private String getEnableModeKey(CommandExecutorProperties properties) {
        Properties botProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot");

        List<String> enabledModeKeys = StartModeFactory.getStartModeKeys().stream()
                .filter(modeKey -> Boolean.parseBoolean(botProperties.getProperty(modeKey)))
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
}

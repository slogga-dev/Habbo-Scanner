package org.slogga.habboscanner.logic.game.commands.Console.commands.start;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.modes.StartBotInActiveRoomsMode;
import org.slogga.habboscanner.logic.game.commands.Console.commands.EnergySavingConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.modes.*;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

public class StartConsoleCommand implements IExecuteCommand {
    @Getter
    private final Map<String, StartMode> startModes = new HashMap<>();

    private boolean hasExecuted = false;

    @Getter
    @Setter
    private boolean isBotRunning = false;

    public StartConsoleCommand() {
        startModes.put("bot.per.id", new StartBotPerIdMode());
        startModes.put("bot.per.owner.name.list", new StartBotPerOwnerNameListMode());
        startModes.put("bot.per.room.id.list", new StartBotPerRoomIdListMode());
        startModes.put("bot.in.active.rooms", new StartBotInActiveRoomsMode());
    }

    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        if (hasExecuted) return;

        isBotRunning = true;
        hasExecuted = true;

        HabboScanner.getInstance().getConfigurator().getRoomEntryHandler().refreshLastRoomAccess();

        boolean isEnergySavingModeEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator().getProperties().get("bot").getProperty("bot.energy.saving.mode.enabled"));

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand)
                CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.ENERGY_SAVING.getKey());

        if (isEnergySavingModeEnabled) energySavingConsoleCommand.setEnergySavingMode(true);

        List<Map.Entry<String, StartMode>> enabledModes = startModes.entrySet().stream()
                .filter(entry -> Boolean.parseBoolean(HabboScanner.getInstance()
                        .getConfigurator()
                        .getProperties()
                        .get("bot")
                        .getProperty(entry.getKey())))
                .collect(Collectors.toList());

        if (enabledModes.size() != 1) {
            String impossibleStartBotMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("message")
                    .getProperty("impossible.start.bot.message");

            HabboActions.sendPrivateMessage(properties.getUserId(), impossibleStartBotMessage);

            return;
        }

        StartMode enabledMode = enabledModes.get(0).getValue();

        CompletableFuture.runAsync(() -> enabledMode.handle(properties.getUserId()));
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.start.command.description");
    }
}
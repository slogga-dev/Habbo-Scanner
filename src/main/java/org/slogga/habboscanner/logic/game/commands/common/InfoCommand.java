package org.slogga.habboscanner.logic.game.commands.common;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.start.*;
import org.slogga.habboscanner.logic.game.commands.common.start.modes.StartBotInActiveRooms;
import org.slogga.habboscanner.logic.game.commands.console.commands.EnergySavingConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;

import java.util.Map;

public abstract class InfoCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot").getProperty("room_furni_active.enabled"));

        Map<String, Command> commands = CommandFactory.commandExecutorInstance.getCommands();

        EnergySavingCommand energySavingCommand = (EnergySavingConsoleCommand) commands.get(CommandKeys.ENERGY_SAVING.getKey());
        boolean energySavingMode = energySavingCommand.isEnergySavingMode();

        StartCommand startConsoleCommand = (StartCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

        StartBotInActiveRooms startBotInActiveRoomsMode = (StartBotInActiveRooms) StartModeFactory
                .getStartModeStrategy("bot.in.active.rooms");
        boolean isProcessingActiveRooms = startBotInActiveRoomsMode.getIsProcessingActiveRooms();

        printStatus(properties, "isRoomFurniActiveEnabled", isRoomFurniActiveEnabled);
        printStatus(properties, "energySavingMode", energySavingMode);
        printStatus(properties, "isBotRunning", isBotRunning);
        printStatus(properties, "isProcessingActiveRooms", isProcessingActiveRooms);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("info.command.description");
    }

    protected abstract void printStatus(CommandExecutorProperties properties, String variableName, boolean isActive);
}

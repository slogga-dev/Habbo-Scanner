package org.slogga.habboscanner.logic.commands.common;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.commands.*;
import org.slogga.habboscanner.logic.commands.common.start.*;
import org.slogga.habboscanner.logic.game.console.commands.EnergySavingConsoleCommand;

import org.slogga.habboscanner.models.enums.CommandKeys;

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

        boolean isProcessingActiveRooms = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.in.active.rooms"));

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

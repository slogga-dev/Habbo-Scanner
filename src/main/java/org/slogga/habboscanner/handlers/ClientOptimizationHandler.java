package org.slogga.habboscanner.handlers;

import java.util.Map;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.logic.commands.*;
import org.slogga.habboscanner.logic.commands.common.EnergySavingCommand;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;

import org.slogga.habboscanner.models.enums.CommandKeys;

public class ClientOptimizationHandler {
    public void onClientOptimization(HMessage message) {
        Map<String, Command> commands = CommandFactory.commandExecutorInstance.getCommands();

        EnergySavingCommand energySavingCommand = (EnergySavingCommand) commands.
                get(CommandKeys.ENERGY_SAVING.getKey());

        if (energySavingCommand == null) return;

        boolean energySavingMode = energySavingCommand.isEnergySavingMode();

        StartCommand startConsoleCommand = (StartCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

        if (!energySavingMode || !isBotRunning) return;

        message.setBlocked(true);
    }
}

package org.slogga.habboscanner.handlers;

import java.util.Map;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.start.StartCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.EnergySavingConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;

public class ClientOptimizationHandler {
    public void onClientOptimization(HMessage message) {
        Map<String, Command> commands = CommandFactory.commandExecutorInstance.getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.
                get(CommandKeys.ENERGY_SAVING.getKey());
        boolean energySavingMode = energySavingConsoleCommand.isEnergySavingMode();

        StartCommand startConsoleCommand = (StartCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

        if (!energySavingMode || !isBotRunning) return;

        message.setBlocked(true);
    }
}

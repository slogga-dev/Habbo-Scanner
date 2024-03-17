package org.slogga.habboscanner.handlers;

import gearth.protocol.HMessage;
import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.EnergySavingConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.Map;

public class ClientOptimizationHandler {
    public void onClientOptimization(HMessage message) {
        Map<String, IExecuteCommand> commands = CommandFactory.commandExecutorInstance.getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(CommandKeys.ENERGY_SAVING.getKey());
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

        if (!energySavingMode || !isBotRunning) return;

        message.setBlocked(true);
    }
}

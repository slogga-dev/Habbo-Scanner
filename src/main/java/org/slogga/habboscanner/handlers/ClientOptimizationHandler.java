package org.slogga.habboscanner.handlers;

import gearth.protocol.HMessage;
import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.EnergySavingConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.Map;

public class ClientOptimizationHandler {
    public void onClientOptimization(HMessage message) {
        Map<String, IConsoleCommand> commands = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(CommandKeys.ENERGY_SAVING.getKey());
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        if (!energySavingMode || !isBotRunning) return;

        message.setBlocked(true);
    }
}

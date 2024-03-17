package org.slogga.habboscanner.logic.game.commands.console.commands;

import java.util.Map;
import java.util.concurrent.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.modes.StartBotInActiveRoomsMode;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.models.CommandKeys;

public class InfoConsoleCommand implements IExecuteCommand {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(
                    HabboScanner
                        .getInstance()
                        .getConfigurator()
                        .getProperties()
                        .get("bot").getProperty("room_furni_active.enabled"));

        Map<String, IExecuteCommand> commands = CommandFactory.commandExecutorInstance.getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(CommandKeys.ENERGY_SAVING.getKey());
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

        StartBotInActiveRoomsMode startBotInActiveRoomsMode = (StartBotInActiveRoomsMode)
                startConsoleCommand.getStartModes().get("bot.in.active.rooms");
        boolean isProcessingActiveRooms = startBotInActiveRoomsMode.getIsProcessingActiveRooms();

        printStatus(properties.getUserId(), "isRoomFurniActiveEnabled", isRoomFurniActiveEnabled, 0);
        printStatus(properties.getUserId(), "energySavingMode", energySavingMode, 500);
        printStatus(properties.getUserId(), "isBotRunning", isBotRunning, 1000);
        printStatus(properties.getUserId(), "isProcessingActiveRooms", isProcessingActiveRooms, 1500);

        scheduler.schedule(() -> HabboActions.sendPrivateMessage(properties.getUserId(), "----------------------"),
                2500, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.info.command.description");
    }

    private void printStatus(int userId, String variableName, boolean isActive, long delay) {
        String statusKey = isActive ? "variable.status.enabled.message" : "variable.status.disabled.message";
        String status = HabboScanner.getInstance().getConfigurator().getProperties().get("message").getProperty(statusKey);

        scheduler.schedule(() -> {
            String message = variableName + " " + status;

            HabboActions.sendPrivateMessage(userId, message);
        }, delay, TimeUnit.MILLISECONDS);
    }
}

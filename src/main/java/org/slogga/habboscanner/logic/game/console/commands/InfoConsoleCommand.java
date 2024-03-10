package org.slogga.habboscanner.logic.game.console.commands;

import java.util.Map;
import java.util.concurrent.*;

import gearth.protocol.HMessage;

import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;

import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.start.modes.StartBotInActiveRoomsMode;

import org.slogga.habboscanner.HabboScanner;

public class InfoConsoleCommand implements IConsoleCommand {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(
                    HabboScanner
                        .getInstance()
                        .getConfigurator()
                        .getProperties()
                        .get("bot").getProperty("room_furni_active.enabled"));

        Map<String, IConsoleCommand> commands = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(":energy_saving");
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(":start");
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        StartBotInActiveRoomsMode startBotInActiveRoomsMode = (StartBotInActiveRoomsMode)
                startConsoleCommand.getStartModes().get("bot.in.active.rooms");
        boolean isProcessingActiveRooms = startBotInActiveRoomsMode.getIsProcessingActiveRooms();

        printStatus(userId, "isRoomFurniActiveEnabled", isRoomFurniActiveEnabled, 0);
        printStatus(userId, "energySavingMode", energySavingMode, 500);
        printStatus(userId, "isBotRunning", isBotRunning, 1000);
        printStatus(userId, "isProcessingActiveRooms", isProcessingActiveRooms, 1500);

        scheduler.schedule(() -> HabboActions.sendPrivateMessage(userId, "----------------------"),
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
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
}

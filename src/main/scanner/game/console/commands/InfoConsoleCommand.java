package scanner.game.console.commands;

import java.util.Map;
import java.util.concurrent.*;

import gearth.protocol.HMessage;

import scanner.game.console.ConsoleCommand;

import scanner.game.console.commands.start.StartConsoleCommand;
import scanner.game.console.commands.start.modes.StartBotInActiveRoomsMode;

import scanner.HabboScanner;

public class InfoConsoleCommand implements ConsoleCommand {
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

        Map<String, ConsoleCommand> commands = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getCommands();

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

        scheduler.schedule(() -> HabboScanner.getInstance()
                .sendPrivateMessage(userId, "----------------------"), 2500, TimeUnit.MILLISECONDS);
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

            HabboScanner.getInstance().sendPrivateMessage(userId, message);
        }, delay, TimeUnit.MILLISECONDS);
    }
}

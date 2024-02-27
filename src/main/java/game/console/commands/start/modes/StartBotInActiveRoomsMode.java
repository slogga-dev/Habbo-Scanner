package game.console.commands.start.modes;

import game.console.commands.start.StartConsoleCommand;
import game.console.commands.start.StartMode;

import scanner.HabboScanner;

import java.util.concurrent.*;

public class StartBotInActiveRoomsMode implements StartMode {
    private boolean isProcessingActiveRooms;

    @Override
    public void handle(int userId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                    .getInstance().getConsoleHandlers().getCommands().get(":start");
            boolean isBotRunning = startConsoleCommand.getIsBotRunning();

            if (!isBotRunning || isProcessingActiveRooms)
                return;

            HabboScanner.getInstance().sendNavigatorSearch("groups", "");

            isProcessingActiveRooms = true;
        }, 0, 2, TimeUnit.SECONDS);
    }

    public boolean getIsProcessingActiveRooms() {
        return isProcessingActiveRooms;
    }

    public void setIsProcessingActiveRooms(boolean isProcessingActiveRooms) {
        this.isProcessingActiveRooms = isProcessingActiveRooms;
    }
}
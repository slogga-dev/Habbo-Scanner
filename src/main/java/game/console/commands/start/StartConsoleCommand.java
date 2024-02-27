package game.console.commands.start;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import game.console.commands.EnergySavingConsoleCommand;
import gearth.protocol.*;

import game.console.ConsoleCommand;

import game.console.commands.start.modes.*;

import scanner.HabboScanner;

public class StartConsoleCommand implements ConsoleCommand {
    private final Map<String, StartMode> startModes = new HashMap<>();

    private boolean isBotRunning = false;

    public StartConsoleCommand() {
        startModes.put("bot.per.id", new StartBotPerIdMode());
        startModes.put("bot.per.owner.name.list", new StartBotPerOwnerNameListMode());
        startModes.put("bot.per.room.id.list", new StartBotPerRoomIdListMode());
        startModes.put("bot.in.active.rooms", new StartBotInActiveRoomsMode());
    }

    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        if (isBotRunning) return;

        isBotRunning = true;

        HabboScanner.getInstance().getRoomInfoHandlers().refreshLastRoomAccess();

        boolean isEnergySavingModeEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("bot.energy.saving.mode.enabled"));

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand)
                HabboScanner.getInstance().getConsoleHandlers().getCommands().get(":energy_saving");

        if (isEnergySavingModeEnabled) energySavingConsoleCommand.setEnergySavingMode(true);

        List<Map.Entry<String, StartMode>> enabledModes = startModes.entrySet().stream()
                .filter(entry -> Boolean.parseBoolean(HabboScanner.getInstance().getBotProperties().getProperty(entry.getKey())))
                .collect(Collectors.toList());

        if (enabledModes.size() != 1) {
            String impossibleStartBotMessage = HabboScanner.getInstance().getMessageProperties().getProperty("impossible.start.bot.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, impossibleStartBotMessage);

            return;
        }

        StartMode enabledMode = enabledModes.get(0).getValue();

        CompletableFuture.runAsync(() -> enabledMode.handle(userId));
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.start.command.description");
    }

    public Map<String, StartMode> getStartModes() {
        return startModes;
    }

    public boolean getIsBotRunning() {
        return isBotRunning;
    }

    public void setIsBotRunning(boolean isBotRunning) {
        this.isBotRunning = isBotRunning;
    }
}
package org.slogga.habboscanner.logic.game.console.commands.start;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.commands.EnergySavingConsoleCommand;
import gearth.protocol.*;

import org.slogga.habboscanner.logic.game.console.IConsoleCommand;

import org.slogga.habboscanner.logic.game.console.commands.start.modes.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

public class StartConsoleCommand implements IConsoleCommand {
    @Getter
    private final Map<String, StartMode> startModes = new HashMap<>();

    @Setter
    private boolean isBotRunning;

    public StartConsoleCommand() {
        startModes.put("bot.per.id", new StartBotPerIdMode());
        startModes.put("bot.per.owner.name.list", new StartBotPerOwnerNameListMode());
        startModes.put("bot.per.room.id.list", new StartBotPerRoomIdListMode());
        startModes.put("bot.in.active.rooms", new StartBotInActiveRoomsMode());
    }


    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);
        resetPreviousCommands();
        if (isBotRunning) return;

        isBotRunning = true;

        HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().refreshLastRoomAccess();

        boolean isEnergySavingModeEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator().getProperties().get("bot").getProperty("bot.energy.saving.mode.enabled"));

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand)
                HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getCommands().get(CommandKeys.ENERGY_SAVING.getKey());

        if (isEnergySavingModeEnabled) energySavingConsoleCommand.setEnergySavingMode(true);

        List<Map.Entry<String, StartMode>> enabledModes = startModes.entrySet().stream()
                .filter(entry -> Boolean.parseBoolean(HabboScanner.getInstance()
                        .getConfigurator()
                        .getProperties()
                        .get("bot")
                        .getProperty(entry.getKey())))
                .collect(Collectors.toList());

        if (enabledModes.size() != 1) {
            String impossibleStartBotMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("message")
                    .getProperty("impossible.start.bot.message");

            HabboActions.sendPrivateMessage(userId, impossibleStartBotMessage);

            return;
        }

        StartMode enabledMode = enabledModes.get(0).getValue();

        CompletableFuture.runAsync(() -> enabledMode.handle(userId));
    }

    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.start.command.description");
    }

    public boolean getIsBotRunning() {
        return isBotRunning;
    }

    // is called plural in case is necessary to reset other commands (for now is only necessary for follow )
    private void resetPreviousCommands(){
        IConsoleCommand command = HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers().getCommands().get(CommandKeys.FOLLOW.getKey());

        if (command == null) return;

        command.resetForStart();
    }
}
package org.slogga.habboscanner.handlers.room;

import org.slf4j.*;

import java.sql.SQLException;

import lombok.Data;

import gearth.protocol.*;

import org.slogga.habboscanner.logic.discord.DiscordBot;

import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;

import org.slogga.habboscanner.models.enums.CommandKeys;

import org.slogga.habboscanner.dao.mysql.data.*;

import org.slogga.habboscanner.logic.game.ItemProcessor;

import org.slogga.habboscanner.HabboScanner;

@Data
public class RoomEntryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RoomEntryHandler.class);

    private ItemProcessor itemProcessor;

    private int roomId;

    private long lastRoomAccess;

    public void onRoomReady(HMessage message) {
        HPacket packet = message.getPacket();

        packet.setReadIndex(15);

        roomId = packet.readInteger();
        itemProcessor = new ItemProcessor();
        lastRoomAccess = System.currentTimeMillis();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        boolean criticalAirCrashWarning = HabboScanner.getInstance().isCriticalAirCrashWarning();

        StartCommand startCommand = (StartCommand) CommandFactory.commandExecutorInstance.getCommands()
                .get(CommandKeys.START.getKey());

        if (startCommand == null) return;

        boolean isBotRunning = startCommand.isBotRunning();

        if (discordBot != null && criticalAirCrashWarning && isBotRunning) {
            String criticalWarningMessage = HabboScanner.getInstance().getConfigurator()
                    .getProperties().get("message").getProperty("discord.bot.critical.warning.message");
            discordBot.getMessageHandler().sendMessageToFeedChannel(criticalWarningMessage);

            HabboScanner.getInstance().setCriticalAirCrashWarning(false);
        }

        if (discordBot != null) {
            String archivingRoomIdMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("message")
                    .getProperty("discord.bot.archiving.room.id.message");
            archivingRoomIdMessage = archivingRoomIdMessage
                    .replace("%roomId%", String.valueOf(roomId));

            discordBot.updateActivity(archivingRoomIdMessage);
        }

        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot")
                .getProperty("room_furni_active.enabled"));

        try {
            if (!isRoomFurniActiveEnabled) return;

            DataActiveDAO.deleteActiveData(roomId);
        } catch (SQLException exception) {
            logger.error(exception.getMessage());
        }
    }

    public void refreshLastRoomAccess() {
        lastRoomAccess = System.currentTimeMillis();
    }
}

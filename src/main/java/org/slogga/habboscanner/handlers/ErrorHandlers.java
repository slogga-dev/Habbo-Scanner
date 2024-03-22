package org.slogga.habboscanner.handlers;

import java.io.IOException;
import java.sql.SQLException;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.handlers.room.RoomEntryHandler;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;

import org.slogga.habboscanner.dao.mysql.LogsDAO;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.logic.game.commands.console.commands.FollowConsoleCommand;
import org.slogga.habboscanner.models.*;

public class ErrorHandlers {
    public void onCantConnect(HMessage message) {
        int packetValue = message.getPacket().readInteger();

        String botBannedFromRoomMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("bot.banned.from.room.message");
        String roomFullMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("room.full.message");

        String errorMessage = (packetValue == 4) ? botBannedFromRoomMessage : roomFullMessage;

        ConsoleHandlers consoleHandlers = HabboScanner.getInstance().getConfigurator().getConsoleHandlers();

        int consoleUserId = consoleHandlers.getUserId();

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand)
                CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        SourceType sourceType = followConsoleCommand.getSourceType();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        switch (sourceType) {
            case HABBO:
                HabboActions.sendPrivateMessage(consoleUserId, errorMessage);
                break;

            case DISCORD:
                if (discordBot == null) return;

                discordBot.sendMessageToFeedChannel(errorMessage);
                break;
        }
    }

    public void onErrorReport(HMessage message) {
        int unknownVariable = message.getPacket().readInteger();
        int errorId = message.getPacket().readInteger();

        RoomEntryHandler roomEntryHandler = HabboScanner.getInstance().getConfigurator().getRoomEntryHandler();

        int roomId = roomEntryHandler.getRoomId();

        roomEntryHandler.refreshLastRoomAccess();

        String serverErrorMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message").getProperty("server.error.message");

        serverErrorMessage = serverErrorMessage
                .replace("%roomId%", String.valueOf(roomId))
                .replace("%errorId%", String.valueOf(errorId));

        try {
            LogsDAO.insertLog(serverErrorMessage);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

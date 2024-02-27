package handlers;

import java.io.IOException;

import java.sql.SQLException;

import gearth.protocol.HMessage;

import scanner.HabboScanner;

import game.console.commands.follow.FollowConsoleCommand;

import database.dao.LogsDAO;

import discord.DiscordBot;

import models.SourceType;

public class ErrorHandlers {
    public void onCantConnect(HMessage message) {
        int packetValue = message.getPacket().readInteger();

        String botBannedFromRoomMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("bot.banned.from.room.message");
        String roomFullMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("room.full.message");

        String errorMessage = (packetValue == 4) ? botBannedFromRoomMessage : roomFullMessage;

        ConsoleHandlers consoleHandlers = HabboScanner.getInstance().getConsoleHandlers();

        int consoleUserId = consoleHandlers.getUserId();

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand)
                consoleHandlers.getCommands().get(":follow");

        SourceType sourceType = followConsoleCommand.getSourceType();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        switch (sourceType) {
            case HABBO:
                HabboScanner.getInstance().sendPrivateMessage(consoleUserId, errorMessage);
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

        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getRoomInfoHandlers();

        int roomId = roomInfoHandlers.getRoomId();

        roomInfoHandlers.refreshLastRoomAccess();

        String serverErrorMessage = HabboScanner.getInstance().getMessageProperties().getProperty("server.error.message");

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

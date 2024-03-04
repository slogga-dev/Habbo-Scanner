package scanner.handlers;

import java.io.IOException;

import java.sql.SQLException;

import gearth.protocol.HMessage;

import scanner.HabboScanner;

import scanner.game.console.commands.follow.FollowConsoleCommand;

import scanner.database.dao.LogsDAO;

import scanner.discord.DiscordBot;

import scanner.models.SourceType;

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

        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers();

        int roomId = roomInfoHandlers.getRoomId();

        roomInfoHandlers.refreshLastRoomAccess();

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

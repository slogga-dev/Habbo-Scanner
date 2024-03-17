package org.slogga.habboscanner.handlers.room;

import gearth.protocol.HMessage;
import lombok.Getter;
import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.RoomsDAO;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.follow.FollowConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.models.CommandKeys;
import org.slogga.habboscanner.models.RoomAccessMode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Getter
public class RoomDetailsHandlers {
    private String currentOwnerName;

    private RoomAccessMode roomAccessMode = RoomAccessMode.UNKNOWN;

    public void onGetGuestRoomResult(HMessage message) {
        boolean unknownVariable = message.getPacket().readBoolean();

        int guestRoomId = message.getPacket().readInteger();
        String roomName = message.getPacket().readString();

        byte[] bytes = roomName.getBytes(StandardCharsets.ISO_8859_1);
        roomName = new String(bytes, StandardCharsets.UTF_8);

        int ownerId = message.getPacket().readInteger();
        currentOwnerName = message.getPacket().readString();

        bytes = currentOwnerName.getBytes(StandardCharsets.ISO_8859_1);

        currentOwnerName = new String(bytes, StandardCharsets.UTF_8);

        try {
            RoomsDAO.insertRoom(guestRoomId, roomName, ownerId, currentOwnerName);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        roomAccessMode = RoomAccessMode.fromValue(message.getPacket().readInteger());

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        if (!followConsoleCommand.isFollowing()) return;

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        switch (roomAccessMode) {
            case LOCKED: {
                String closedRoomAccessMessage = HabboScanner.getInstance()
                        .getConfigurator().getProperties().get("message").getProperty("closed.room.access.message");

                HabboActions.sendPrivateMessage(consoleUserId, closedRoomAccessMessage);

                followConsoleCommand.initiateBotAndRefreshRoomAccess();

                break;
            }

            case UNKNOWN: {
                String noRoomAccessMessage = HabboScanner.getInstance()
                        .getConfigurator().getProperties().get("message").getProperty("no.room.access.message");

                HabboActions.sendPrivateMessage(consoleUserId, noRoomAccessMessage);

                followConsoleCommand.initiateBotAndRefreshRoomAccess();

                break;
            }
        }
    }

    public void onRoomVisualizationSettings(HMessage message) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());

        boolean isBotRunning = startConsoleCommand.isBotRunning();

        boolean isBotInActiveRooms = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot")
                .getProperty("bot.in.active.rooms"));

        if (!isBotRunning & !isBotInActiveRooms) return;

        HabboActions.goToHotelView();
    }
}

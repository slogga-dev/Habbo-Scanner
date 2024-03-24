package org.slogga.habboscanner.handlers.room;

import java.io.IOException;
import java.sql.SQLException;

import gearth.protocol.HMessage;

import lombok.Getter;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.RoomsDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.common.start.StartCommand;

import org.slogga.habboscanner.logic.game.commands.console.commands.FollowConsoleCommand;
import org.slogga.habboscanner.models.*;
import org.slogga.habboscanner.utils.UTF8Utils;

@Getter
public class RoomDetailsHandlers {
    private RoomDetails roomDetails;

    private final RoomAccessMode roomAccessMode = RoomAccessMode.UNKNOWN;

    public void onGetGuestRoomResult(HMessage message) {
        roomDetails = extractRoomDetails(message);

        handleRoomAccess(roomDetails);
    }

    public void onRoomVisualizationSettings(HMessage message) {
        StartCommand startCommand = (StartCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.START.getKey());

        if (startCommand == null) return;

        boolean isBotRunning = startCommand.isBotRunning();

        boolean isBotInActiveRooms = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot")
                .getProperty("bot.in.active.rooms"));

        if (!isBotRunning & !isBotInActiveRooms) return;

        HabboActions.goToHotelView();
    }

    private RoomDetails extractRoomDetails(HMessage message) {
        boolean unknownVariable = message.getPacket().readBoolean();
        int guestRoomId = message.getPacket().readInteger();
        String roomName = UTF8Utils.convertToUTF8(message.getPacket().readString());
        int ownerId = message.getPacket().readInteger();
        String currentOwnerName = UTF8Utils.convertToUTF8(message.getPacket().readString());

        try {
            RoomsDAO.insertRoom(guestRoomId, roomName, ownerId, currentOwnerName);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        RoomAccessMode roomAccessMode = RoomAccessMode.fromValue(message.getPacket().readInteger());

        return new RoomDetails(guestRoomId, roomName, ownerId, currentOwnerName, roomAccessMode);
    }

    private void handleRoomAccess(RoomDetails roomDetails) {
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.FOLLOW.getKey());

        if (followConsoleCommand == null || !followConsoleCommand.isFollowing()) return;

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        switch (roomDetails.getRoomAccessMode()) {
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
}

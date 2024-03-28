package org.slogga.habboscanner.logic.game;

import gearth.protocol.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.console.commands.channel.ChannelConsoleCommand;
import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.MessageChannel;

public class HabboActions {
    public static void moveToRoom(int roomId) {
        HPacket packet = new HPacket("GetGuestRoom", HMessage.Direction.TOSERVER,
                roomId, 0, 1);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void sendMessage(int userId, String message) {
        MessageChannel defaultChannel = MessageChannel.fromValue(HabboScanner.getInstance()
                .getConfigurator().getProperties().get("bot")
                .getProperty("bot.default.message.channel"));

        ChannelConsoleCommand channelConsoleCommand = (ChannelConsoleCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.CHANNEL.getKey());

        if (channelConsoleCommand.getChannel() == null) channelConsoleCommand.setChannel(defaultChannel);

        switch (channelConsoleCommand.getChannel()) {
            case CHAT:
                shoutMessage(message);
                break;

            case CONSOLE:
                sendPrivateMessage(userId, message);
                break;
        }
    }

    public static void whisperMessage(String text) {
        HPacket packet = new HPacket("Whisper", HMessage.Direction.TOCLIENT,
                -1, text, 0, 30, 0, -1);

        HabboScanner.getInstance().sendToClient(packet);
    }

    public static void sendNavigatorSearch(String searchType, String searchValue) {
        HPacket packet = new HPacket("NewNavigatorSearch",
                HMessage.Direction.TOSERVER, searchType, searchValue);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void followUser(int userId) {
        HPacket packet = new HPacket("FollowFriend",
                HMessage.Direction.TOSERVER, userId);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void sendAvatarExpression(int expressionId) {
        HPacket packet = new HPacket("AvatarExpression",
                HMessage.Direction.TOSERVER, expressionId);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void goToHotelView() {
        HPacket packet = new HPacket("Quit", HMessage.Direction.TOSERVER,
                1);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void dance(int danceId) {
        HPacket packet = new HPacket("Dance", HMessage.Direction.TOSERVER, danceId);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void sign(int signId) {
        HPacket packet = new HPacket("Sign", HMessage.Direction.TOSERVER, signId);

        HabboScanner.getInstance().sendToServer(packet);
    }

    public static void sendPrivateMessage(int userId, String message) {
        HPacket packet = new HPacket("SendMsg", HMessage.Direction.TOSERVER,
                userId, message);

        HabboScanner.getInstance().sendToServer(packet);
    }

    private static void shoutMessage(String message) {
        HPacket packet = new HPacket("Shout", HMessage.Direction.TOSERVER,
                message, 0);

        HabboScanner.getInstance().sendToServer(packet);
    }
}

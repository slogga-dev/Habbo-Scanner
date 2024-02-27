package discord.commands;

import java.util.Objects;

import java.util.Properties;
import java.util.concurrent.*;

import discord.DiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import game.console.commands.follow.*;
import game.console.commands.start.StartConsoleCommand;

import handlers.RoomInfoHandlers;

import models.*;

import scanner.HabboScanner;

public class FollowDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String mode = Objects.requireNonNull(event.getOption("mode")).getAsString();

        Properties messageProperties = HabboScanner.getInstance().getMessageProperties();

        if (!mode.equals("furni_info") && !mode.equals("auction")) {
            String invalidModeReply = messageProperties.getProperty("invalid.mode.message");

            event.reply(invalidModeReply).setEphemeral(true).queue();

            return;
        }

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":start");

        if (!startConsoleCommand.getIsBotRunning()) {
            String botNotActiveReply = messageProperties.getProperty("bot.not.active.message");

            event.reply(botNotActiveReply).queue();

            return;
        }

        startConsoleCommand.setIsBotRunning(false);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":follow");

        followConsoleCommand.setSourceType(SourceType.DISCORD);

        FollowingAction followingAction = FollowingAction.fromValue(mode);

        followConsoleCommand.setFollowingAction(followingAction);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        String discordUserId = event.getUser().getId();

        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        String botMessageFollowing = HabboScanner.getInstance().getMessageProperties().getProperty("bot.following.message");
        String[] botMessageFollowingArray = botMessageFollowing.split("---");

        int randomIndex = (int) (Math.random() * botMessageFollowingArray.length);
        botMessageFollowing = botMessageFollowingArray[randomIndex];

        event.reply(botMessageFollowing).queue();

        HabboScanner.getInstance().followUser(habboUserId);
        HabboScanner.getInstance().getConsoleHandlers().setUserId(habboUserId);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getRoomInfoHandlers();
            RoomAccessMode roomAccessMode = roomInfoHandlers.getRoomAccessMode();

            switch (roomAccessMode) {
                case OPEN: {
                    followConsoleCommand.setIsFollowingFriend(true);

                    FollowingActionMode actionMode = followConsoleCommand.getActionModes().get(followingAction);

                    actionMode.handle();

                    break;
                }

                case LOCKED: {
                    String closedRoomAccessMessage = HabboScanner.getInstance().getMessageProperties().getProperty("closed.room.access.message");

                    HabboScanner.getInstance().sendPrivateMessage(habboUserId, closedRoomAccessMessage);

                    break;
                }

                case UNKNOWN: {
                    String noRoomAccessMessage = HabboScanner.getInstance().getMessageProperties().getProperty("no.room.access.message");

                    HabboScanner.getInstance().sendPrivateMessage(habboUserId, noRoomAccessMessage);

                    break;
                }
            }
        }, 1, TimeUnit.SECONDS);
    }
}

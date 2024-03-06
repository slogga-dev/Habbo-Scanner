package scanner.discord.commands;

import java.util.Objects;

import java.util.Properties;
import java.util.concurrent.*;

import scanner.discord.DiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import scanner.discord.DiscordCommand;

import scanner.game.console.commands.follow.*;
import scanner.game.console.commands.start.StartConsoleCommand;

import scanner.handlers.RoomInfoHandlers;

import scanner.models.*;

import scanner.HabboScanner;

public class FollowDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String mode = Objects.requireNonNull(event.getOption("mode")).getAsString();

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        if (!mode.equals("furni_info") && !mode.equals("auction")) {
            String invalidModeMessage = messageProperties.getProperty("invalid.mode.message");

            event.reply(invalidModeMessage).setEphemeral(true).queue();

            return;
        }

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers().getCommands().get(":start");

        if (!startConsoleCommand.getIsBotRunning()) {
            String botNotActiveMessage = messageProperties.getProperty("bot.not.active.message");

            event.reply(botNotActiveMessage).queue();

            return;
        }

        startConsoleCommand.setIsBotRunning(false);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance()
                .getConfigurator().getConsoleHandlers().getCommands().get(":follow");

        followConsoleCommand.setSourceType(SourceType.DISCORD);

        FollowingAction followingAction = FollowingAction.fromValue(mode);

        followConsoleCommand.setFollowingAction(followingAction);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        String discordUserId = event.getUser().getId();

        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        String botMessageFollowing = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message").getProperty("bot.following.message");
        String[] botMessageFollowingArray = botMessageFollowing.split("---");

        int randomIndex = (int) (Math.random() * botMessageFollowingArray.length);
        botMessageFollowing = botMessageFollowingArray[randomIndex];

        event.reply(botMessageFollowing).queue();

        HabboScanner.getInstance().followUser(habboUserId);
        HabboScanner.getInstance().getConfigurator().getConsoleHandlers().setUserId(habboUserId);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers();
            RoomAccessMode roomAccessMode = roomInfoHandlers.getRoomAccessMode();

            switch (roomAccessMode) {
                case OPEN: {
                    followConsoleCommand.setFollowing(true);

                    FollowingActionMode actionMode = followConsoleCommand.getActionModes().get(followingAction);

                    actionMode.handle();

                    break;
                }

                case LOCKED: {

                    String closedRoomAccessMessage = HabboScanner.getInstance().getConfigurator()
                            .getProperties().get("message").getProperty("closed.room.access.message");

                    HabboScanner.getInstance().sendPrivateMessage(habboUserId, closedRoomAccessMessage);

                    break;
                }

                case UNKNOWN: {

                    String noRoomAccessMessage = HabboScanner.getInstance()
                            .getConfigurator()
                            .getProperties()
                            .get("message")
                            .getProperty("no.room.access.message");

                    HabboScanner.getInstance().sendPrivateMessage(habboUserId, noRoomAccessMessage);

                    break;
                }
            }
        }, 1, TimeUnit.SECONDS);
    }
}

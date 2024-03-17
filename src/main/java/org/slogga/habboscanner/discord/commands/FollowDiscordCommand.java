package org.slogga.habboscanner.discord.commands;

import java.util.Objects;

import java.util.Properties;
import java.util.concurrent.*;

import org.slogga.habboscanner.discord.DiscordBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowingActionMode;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.models.*;

import org.slogga.habboscanner.HabboScanner;

public class FollowDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String mode = Objects.requireNonNull(event.getOption("mode")).getAsString();

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        if (!mode.equals("furni_info") && !mode.equals("auction")) {
            String invalidModeMessage = messageProperties.getProperty("invalid.mode.message");

            event.reply(invalidModeMessage).setEphemeral(true).queue();

            return;
        }

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.
                getCommands().get(CommandKeys.START.getKey());

        if (!startConsoleCommand.isBotRunning()) {
            String botNotActiveMessage = messageProperties.getProperty("bot.not.active.message");

            event.reply(botNotActiveMessage).queue();

            return;
        }

        startConsoleCommand.setBotRunning(false);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

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

        HabboActions.followUser(habboUserId);
        HabboScanner.getInstance().getConfigurator().getConsoleHandlers().setUserId(habboUserId);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            RoomAccessMode roomAccessMode = HabboScanner.getInstance().getConfigurator().getRoomDetailsHandlers().getRoomAccessMode();

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

                    HabboActions.sendPrivateMessage(habboUserId, closedRoomAccessMessage);

                    break;
                }

                case UNKNOWN: {
                    String noRoomAccessMessage = HabboScanner.getInstance()
                            .getConfigurator()
                            .getProperties()
                            .get("message")
                            .getProperty("no.room.access.message");

                    HabboActions.sendPrivateMessage(habboUserId, noRoomAccessMessage);

                    break;
                }
            }
        }, 1, TimeUnit.SECONDS);
    }
}

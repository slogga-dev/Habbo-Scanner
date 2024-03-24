package org.slogga.habboscanner.logic.game.commands.common.follow;

import java.util.concurrent.*;

import lombok.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.*;

import org.slogga.habboscanner.logic.game.commands.common.start.StartCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.FollowingAction;
import org.slogga.habboscanner.models.enums.SourceType;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class FollowCommand extends Command {
    protected FollowingAction followingAction;

    protected boolean isFollowing;
    protected SourceType sourceType;

    public FollowCommand() {
        this.isFollowing = false;
    }

    @Override
    public void execute(CommandExecutorProperties properties) {
        StartCommand startCommand = (StartCommand) CommandFactory.commandExecutorInstance.
                getCommands().get(CommandKeys.START.getKey());

        if (!startCommand.isBotRunning() || isFollowing) return;

        // Set :start command to false, so it cannot be called by another user.
        startCommand.setBotRunning(false);

        isFollowing = true;

        HabboScanner.getInstance().getConfigurator().getRoomEntryHandler().refreshLastRoomAccess();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> HabboActions.followUser(properties.getUserId()), 1, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("follow.command.description");
    }
}


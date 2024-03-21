package org.slogga.habboscanner.logic.game.commands.console;

import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.LogoutCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.*;
import org.slogga.habboscanner.logic.game.commands.console.commands.convert.ConvertConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.follow.FollowConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;

import java.util.Arrays;

public class ConsoleCommandExecutor extends CommandExecutor {
    public ConsoleCommandExecutor(CommandExecutorProperties properties) {
        super(properties);

        setupCommands();
    }

    @Override
    public void setupCommands() {
        Arrays.stream(CommandKeys.values()).forEach(key ->
                commands.put(":" + key.getKey(), createCommand(key))
        );
    }

    private Command createCommand(CommandKeys key) {
        switch (key) {
//            case START: return new StartConsoleCommand();
//            case PAUSE: return new PauseConsoleCommand();
//            case RESUME: return new ResumeConsoleCommand();
//            case FOLLOW: return new FollowConsoleCommand();
//            case INFO: return new InfoConsoleCommand();
//            case CONVERT: return new ConvertConsoleCommand();
//            case UPDATE: return new UpdateConsoleCommand();
//            case MAKESAY: return new MakeSayCommand();
            case LOGOUT: return new LogoutCommand() ;
//            case ENERGY_SAVING: return new EnergySavingConsoleCommand();
//            case COMMANDS: return new CommandsConsoleCommand();
            default: return null;
        }
    }
}

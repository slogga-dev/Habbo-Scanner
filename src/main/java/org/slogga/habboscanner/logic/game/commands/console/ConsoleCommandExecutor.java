package org.slogga.habboscanner.logic.game.commands.console;

import java.util.Arrays;

import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.console.commands.*;

import org.slogga.habboscanner.models.CommandKeys;

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
            case START: return new StartConsoleCommand();
//            case PAUSE: return new PauseConsoleCommand();
//            case RESUME: return new ResumeConsoleCommand();
//            case FOLLOW: return new FollowConsoleCommand();
//            case INFO: return new InfoConsoleCommand();
//            case CONVERT: return new ConvertConsoleCommand();
//            case UPDATE: return new UpdateConsoleCommand();
//            case MAKESAY: return new MakeSayCommand();
            case SHUTDOWN: return new ShutdownConsoleCommand();
//            case ENERGY_SAVING: return new EnergySavingConsoleCommand();
//            case COMMANDS: return new CommandsConsoleCommand();
            default:
                return null;
        }
    }
}

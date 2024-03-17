package org.slogga.habboscanner.logic.game.commands.Console;

import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.Console.commands.*;
import org.slogga.habboscanner.logic.game.commands.Console.commands.convert.ConvertConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;
public class ConsoleCommandExecutor extends CommandExecutor {
    public ConsoleCommandExecutor(CommandExecutorProperties properties) {
        super(properties);
        setCommands();
    }
    public ConsoleCommandExecutor(){
        super();
    }

    @Override
    public void setCommands() {
            commands.put(CommandKeys.START.getKey(), new StartConsoleCommand());
            commands.put(CommandKeys.PAUSE.getKey(), new PauseConsoleCommand());
            commands.put(CommandKeys.RESUME.getKey(), new ResumeConsoleCommand());
            commands.put(CommandKeys.FOLLOW.getKey(), new FollowConsoleCommand());
            commands.put(CommandKeys.INFO.getKey(), new InfoConsoleCommand());
            commands.put(CommandKeys.CONVERT.getKey(), new ConvertConsoleCommand());
            commands.put(CommandKeys.UPDATE.getKey(), new UpdateConsoleCommand());
            commands.put(CommandKeys.MAKESAY.getKey(), new MakeSayCommand());
            commands.put(CommandKeys.LOGOUT.getKey(), new LogoutConsoleCommand());
            commands.put(CommandKeys.ENERGY_SAVING.getKey(), new EnergySavingConsoleCommand());
            commands.put(CommandKeys.COMMANDS.getKey(), new CommandsConsoleCommand());
    }

}

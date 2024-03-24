package org.slogga.habboscanner.logic.game.commands.console.commands;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.common.convert.ConvertCommand;
import org.slogga.habboscanner.models.enums.ConvertFile;

public class ConvertConsoleCommand extends ConvertCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        String[] arguments = properties.getMessageText().split(" ");

        if (arguments.length < 2) {
            String noParametersMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                    .getProperty("convert.command.no.parameters.message");

            HabboActions.sendPrivateMessage(properties.getUserId(), noParametersMessage);

            return;
        }

        convertFile = ConvertFile.fromValue(arguments[0]);

        super.execute(properties);
    }
}

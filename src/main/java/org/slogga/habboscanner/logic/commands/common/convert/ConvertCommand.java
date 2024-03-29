package org.slogga.habboscanner.logic.commands.common.convert;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.commands.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.enums.ConvertFile;

public class ConvertCommand extends Command {
    protected ConvertFile convertFile;

    @Override
    public void execute(CommandExecutorProperties properties) {
        Converter converter = ConvertFileFactory.getFollowingActionStrategy(convertFile);

        if (converter == null) {
            String invalidParameterMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                    .getProperty("convert.command.invalid.parameter.message");

            HabboActions.sendMessage(properties.getUserId(), invalidParameterMessage);

            return;
        }

        converter.execute(properties, convertFile);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("convert.command.description");
    }
}

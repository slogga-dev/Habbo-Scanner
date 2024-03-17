package org.slogga.habboscanner.logic.game.commands.console.commands.convert;

import java.util.*;

import lombok.Getter;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.console.commands.convert.files.*;
import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;

@Getter
public class ConvertConsoleCommand implements IExecuteCommand {
    private final Map<String, ConvertFile> convertFiles = new HashMap<>();

    public ConvertConsoleCommand() {
        convertFiles.put("items", new ConvertItemsFile());
        convertFiles.put("timeline", new ConvertTimelineFile());
    }

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

        String file = arguments[1];
        ConvertFile convertFile = convertFiles.get(file);

        if (convertFile == null) {
            String invalidParameterMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                    .getProperty("convert.command.invalid.parameter.message");

            HabboActions.sendPrivateMessage(properties.getUserId(), invalidParameterMessage);

            return;
        }

        convertFile.handle(properties.getUserId());
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.convert.command.description");
    }

}

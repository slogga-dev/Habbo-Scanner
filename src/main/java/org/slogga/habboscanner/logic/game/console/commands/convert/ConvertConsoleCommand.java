package org.slogga.habboscanner.logic.game.console.commands.convert;

import java.util.*;

import gearth.protocol.HMessage;

import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;

import org.slogga.habboscanner.logic.game.console.commands.convert.files.*;

import org.slogga.habboscanner.HabboScanner;

public class ConvertConsoleCommand implements IConsoleCommand {
    private final Map<String, ConvertFile> convertFiles = new HashMap<>();

    public ConvertConsoleCommand() {
        convertFiles.put("items", new ConvertItemsFile());
        convertFiles.put("timeline", new ConvertTimelineFile());
    }

    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        String[] arguments = messageText.split(" ");

        if (arguments.length < 2) {
            String noParametersMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                    .getProperty("convert.command.no.parameters.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, noParametersMessage);

            return;
        }

        String mode = arguments[1];
        ConvertFile convertFile = convertFiles.get(mode);

        if (convertFile == null) {
            String invalidParameterMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                    .getProperty("convert.command.invalid.parameter.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, invalidParameterMessage);

            return;
        }

        convertFile.handle(userId);
    }

    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.convert.command.description");
    }

    public Map<String, ConvertFile> getConvertFiles() {
        return convertFiles;
    }
}

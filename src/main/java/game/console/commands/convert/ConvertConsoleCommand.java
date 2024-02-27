package game.console.commands.convert;

import java.util.*;

import gearth.protocol.HMessage;

import game.console.ConsoleCommand;

import game.console.commands.convert.files.*;

import scanner.HabboScanner;

public class ConvertConsoleCommand implements ConsoleCommand {
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
            String noParametersMessage = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("convert.command.no.parameters.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, noParametersMessage);

            return;
        }

        String mode = arguments[1];
        ConvertFile convertFile = convertFiles.get(mode);

        if (convertFile == null) {
            String invalidParameterMessage = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("convert.command.invalid.parameter.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, invalidParameterMessage);

            return;
        }

        convertFile.handle(userId);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.convert.command.description");
    }

    public Map<String, ConvertFile> getConvertFiles() {
        return convertFiles;
    }
}

package org.slogga.habboscanner.logic.game.console.commands.channel.modes;

import org.slogga.habboscanner.logic.commands.CommandFactory;

import org.slogga.habboscanner.logic.game.console.commands.channel.*;

import org.slogga.habboscanner.models.enums.*;

public class ConsoleChannelMode implements IChannelMode {
    @Override
    public void execute() {
        ChannelConsoleCommand channelConsoleCommand = (ChannelConsoleCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.CHANNEL.getKey());

        channelConsoleCommand.setChannel(MessageChannel.CONSOLE);
    }
}

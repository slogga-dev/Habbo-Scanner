package org.slogga.habboscanner.logic.game.commands.Console;

import gearth.protocol.HMessage;

public interface IConsoleCommand {
    void execute(HMessage message, String messageText, int userId);
    String getDescription();
}

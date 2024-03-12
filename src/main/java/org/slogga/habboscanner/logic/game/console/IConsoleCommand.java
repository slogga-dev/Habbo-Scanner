package org.slogga.habboscanner.logic.game.console;

import gearth.protocol.HMessage;

public interface IConsoleCommand {
    void execute(HMessage message, String messageText, int userId);
    String getDescription();
}

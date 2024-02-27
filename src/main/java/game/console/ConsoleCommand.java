package game.console;

import gearth.protocol.HMessage;

import java.sql.SQLException;

public interface ConsoleCommand {
    void execute(HMessage message, String messageText, int userId);

    String getDescription();
}

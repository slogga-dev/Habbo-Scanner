package scanner.game.console;

import gearth.protocol.HMessage;

public interface IConsoleCommand {
    void execute(HMessage message, String messageText, int userId);
    void resetForStart();
    String getDescription();
}

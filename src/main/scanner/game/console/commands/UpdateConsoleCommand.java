package scanner.game.console.commands;

import java.io.IOException;
import java.sql.SQLException;

import gearth.protocol.HMessage;

import org.apache.commons.lang3.NotImplementedException;
import scanner.database.dao.items.ItemsDAO;

import scanner.game.console.IConsoleCommand;

import scanner.HabboScanner;

public class UpdateConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        HabboScanner.getInstance().getConfigurator().getProperties().get("bot");

        try {
            HabboScanner.getInstance().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateInformationMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("update.completed.message");

        HabboScanner.getInstance().sendPrivateMessage(userId, updateInformationMessage);

        message.setBlocked(true);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.update.command.description");
    }
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
}

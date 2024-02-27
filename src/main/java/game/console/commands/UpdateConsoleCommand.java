package game.console.commands;

import java.io.IOException;
import java.sql.SQLException;

import gearth.protocol.HMessage;

import database.dao.items.ItemsDAO;

import game.console.ConsoleCommand;

import scanner.HabboScanner;

public class UpdateConsoleCommand implements ConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        HabboScanner.getInstance().loadBotProperties();

        try {
            HabboScanner.getInstance().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateInformationMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("update.completed.message");

        HabboScanner.getInstance().sendPrivateMessage(userId, updateInformationMessage);

        message.setBlocked(true);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.update.command.description");
    }
}

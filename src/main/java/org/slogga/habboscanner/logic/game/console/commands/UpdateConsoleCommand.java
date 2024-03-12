package org.slogga.habboscanner.logic.game.console.commands;

import java.io.IOException;
import java.sql.SQLException;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.dao.mysql.items.ItemsDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;

import org.slogga.habboscanner.HabboScanner;

public class UpdateConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        HabboScanner.getInstance().getConfigurator().getProperties().get("bot");

        try {
            HabboScanner.getInstance().getFurnidataConfigurator().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateInformationMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("update.completed.message");

        HabboActions.sendPrivateMessage(userId, updateInformationMessage);

        message.setBlocked(true);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.update.command.description");
    }
}

package org.slogga.habboscanner.logic.game.commands.common;

import java.io.IOException;
import java.sql.SQLException;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.dao.mysql.items.ItemsDAO;

import org.slogga.habboscanner.logic.game.commands.*;

public class UpdateCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        HabboScanner.getInstance().getConfigurator().loadProperty("bot");

        try {
            HabboScanner.getInstance().getFurnidataConfigurator().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateInformationMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("update.completed.message");

        sendMessage(updateInformationMessage, properties);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("update.command.description");
    }
}

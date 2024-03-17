package org.slogga.habboscanner.logic.game.commands.Console.commands;

import java.io.IOException;
import java.sql.SQLException;

import org.slogga.habboscanner.dao.mysql.items.ItemsDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;

public class UpdateConsoleCommand implements IExecuteCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        HabboScanner.getInstance().getConfigurator().getProperties().get("bot");

        try {
            HabboScanner.getInstance().getFurnidataConfigurator().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateInformationMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("update.completed.message");

        HabboActions.sendPrivateMessage(properties.getUserId(), updateInformationMessage);

        properties.getMessage().setBlocked(true);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.update.command.description");
    }
}

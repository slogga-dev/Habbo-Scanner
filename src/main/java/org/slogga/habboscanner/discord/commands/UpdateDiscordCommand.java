package org.slogga.habboscanner.discord.commands;

import java.io.IOException;
import java.sql.SQLException;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.dao.mysql.items.ItemsDAO;

import org.slogga.habboscanner.HabboScanner;

public class UpdateDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        HabboScanner.getInstance().getConfigurator().loadProperty("bot");

        try {
            HabboScanner.getInstance().getFurnidataConfigurator().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateCompletedMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("update.completed.message");

        event.reply(updateCompletedMessage).queue();
    }
}
package discord.commands;

import java.io.IOException;
import java.sql.SQLException;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import database.dao.items.ItemsDAO;

import scanner.HabboScanner;

public class UpdateDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        HabboScanner.getInstance().loadBotProperties();

        try {
            HabboScanner.getInstance().setItems(ItemsDAO.fetchItems());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        String updateCompletedMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("update.completed.message");

        event.reply(updateCompletedMessage).queue();
    }
}
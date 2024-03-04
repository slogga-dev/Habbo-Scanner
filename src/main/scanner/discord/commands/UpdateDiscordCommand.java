package scanner.discord.commands;

import java.io.IOException;
import java.sql.SQLException;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import scanner.discord.DiscordCommand;

import scanner.database.dao.items.ItemsDAO;

import scanner.HabboScanner;

public class UpdateDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        HabboScanner.getInstance().getConfigurator().loadProperty("bot");

        try {
            HabboScanner.getInstance().setItems(ItemsDAO.fetchItems());
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
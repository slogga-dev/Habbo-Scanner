package org.slogga.habboscanner.logic.discord.commands.commands;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.common.start.StartCommand;

public class StartDiscordCommand extends StartCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        SlashCommandInteractionEvent event = properties.getEvent();

        if (isBotRunning()) {
            String botAlreadySearchingMessage = HabboScanner.getInstance()
                    .getConfigurator().getProperties().get("message").getProperty("bot.already.searching.message");

            event.reply(botAlreadySearchingMessage).queue();

            return;
        }

        super.execute(properties);
    }
}

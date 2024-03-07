package org.slogga.habboscanner.discord.commands;

import java.util.concurrent.*;

import org.slogga.habboscanner.discord.IDiscordCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slogga.habboscanner.HabboScanner;

public class LogoutDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String logoutMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message").getProperty("bot.logout.message");

        event.reply(logoutMessage).queue();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }
}
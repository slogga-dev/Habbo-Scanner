package discord.commands;

import java.util.concurrent.*;

import discord.DiscordCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import scanner.HabboScanner;

public class LogoutDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String logoutMessage = HabboScanner.getInstance().getMessageProperties().getProperty("bot.logout.message");

        event.reply(logoutMessage).queue();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }
}
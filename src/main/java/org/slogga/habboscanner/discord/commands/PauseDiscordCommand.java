package org.slogga.habboscanner.discord.commands;

import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.DiscordCommand;

import org.slogga.habboscanner.HabboScanner;

public class PauseDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers()
                .getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(false);

        String relaxMomentMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("relax.moment.message");

        event.reply(relaxMomentMessage).queue();
    }
}
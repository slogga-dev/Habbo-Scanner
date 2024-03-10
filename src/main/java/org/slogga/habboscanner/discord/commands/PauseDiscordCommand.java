package org.slogga.habboscanner.discord.commands;

import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.HabboScanner;

public class PauseDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers()
                .getCommands().get(":start");
        startConsoleCommand.setBotRunning(false);

        String relaxMomentMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("relax.moment.message");

        event.reply(relaxMomentMessage).queue();
    }
}
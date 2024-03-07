package org.slogga.habboscanner.discord.commands;

import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.DiscordCommand;

import org.slogga.habboscanner.HabboScanner;

public class ResumeDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                .getInstance()
                .getConfigurator()
                .getConsoleHandlers()
                .getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(true);

        String botResumeOperationMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("bot.resume.operation.reply");

        event.reply(botResumeOperationMessage).queue();
    }
}
package discord.commands;

import game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import scanner.HabboScanner;

public class ResumeDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                .getInstance().getConsoleHandlers().getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(true);

        String botResumeOperationReply = HabboScanner.getInstance()
                .getMessageProperties().getProperty("bot.resume.operation.reply");

        event.reply(botResumeOperationReply).queue();
    }
}
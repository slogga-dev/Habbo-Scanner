package discord.commands;

import game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import scanner.HabboScanner;

public class PauseDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(false);

        String relaxMomentMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("relax.moment.message");

        event.reply(relaxMomentMessage).queue();
    }
}
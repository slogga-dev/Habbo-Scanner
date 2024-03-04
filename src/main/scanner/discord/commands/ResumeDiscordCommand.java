package scanner.discord.commands;

import scanner.game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import scanner.discord.DiscordCommand;

import scanner.HabboScanner;

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
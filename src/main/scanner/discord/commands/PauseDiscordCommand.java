package scanner.discord.commands;

import scanner.game.console.commands.start.StartConsoleCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import scanner.discord.DiscordCommand;

import scanner.HabboScanner;

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
package scanner.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import scanner.discord.DiscordCommand;

import scanner.game.console.commands.EnergySavingConsoleCommand;

import scanner.HabboScanner;

public class EnergySavingDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand)
                HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getCommands().get(":energy_saving");

        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        energySavingConsoleCommand.setEnergySavingMode(!energySavingMode);

        String statusMessage = energySavingMode ? "Risparmio energetico attivato." :
                "Risparmio energetico disattivato.";

        event.reply(statusMessage).queue();
    }
}

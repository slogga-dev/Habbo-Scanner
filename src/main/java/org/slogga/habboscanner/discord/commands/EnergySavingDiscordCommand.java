package org.slogga.habboscanner.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.IDiscordCommand;

import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.EnergySavingConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.Properties;

public class EnergySavingDiscordCommand implements IDiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand)
                CommandFactory.commandExecutorInstance.getCommands()
                        .get(CommandKeys.ENERGY_SAVING.getKey());

        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        energySavingConsoleCommand.setEnergySavingMode(!energySavingMode);

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        String energySavingModeEnabledMessage = messageProperties.getProperty("energy.saving.mode.enabled");
        String energySavingModeDisabledMessage = messageProperties.getProperty("energy.saving.mode.disabled");

        String statusMessage = energySavingMode ? energySavingModeEnabledMessage : energySavingModeDisabledMessage;

        event.reply(statusMessage).queue();
    }
}

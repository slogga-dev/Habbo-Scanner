package org.slogga.habboscanner.logic.game.commands.Console.commands;

import gearth.protocol.HMessage;

import lombok.Setter;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;

import java.util.Properties;

@Setter
public class EnergySavingConsoleCommand implements IExecuteCommand {
    private boolean energySavingMode;

    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        energySavingMode = !energySavingMode;

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        String modeEnabledMessage = messageProperties.getProperty("energy.saving.mode.enabled");
        String modeDisabledMessage = messageProperties.getProperty("energy.saving.mode.disabled");

        String statusMessage = energySavingMode ? modeEnabledMessage : modeDisabledMessage;

        HabboActions.sendPrivateMessage(properties.getUserId(), statusMessage);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.energy_saving.command.description");
    }

    public boolean getEnergySavingMode() {
        return energySavingMode;
    }

}

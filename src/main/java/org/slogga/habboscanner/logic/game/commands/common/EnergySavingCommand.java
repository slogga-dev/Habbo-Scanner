package org.slogga.habboscanner.logic.game.commands.common;

import java.util.Properties;

import lombok.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.game.commands.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnergySavingCommand extends Command {
    private boolean energySavingMode;

    @Override
    public void execute(CommandExecutorProperties properties) {
        energySavingMode = !energySavingMode;

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        String modeEnabledMessage = messageProperties.getProperty("energy.saving.mode.enabled");
        String modeDisabledMessage = messageProperties.getProperty("energy.saving.mode.disabled");

        String statusMessage = energySavingMode ? modeEnabledMessage : modeDisabledMessage;

        sendMessage(statusMessage, properties);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("energy_saving.command.description");
    }
}

package org.slogga.habboscanner.logic.game.console.commands;

import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import gearth.protocol.HMessage;
import org.slogga.habboscanner.HabboScanner;

import java.util.Properties;

public class EnergySavingConsoleCommand implements IConsoleCommand {
    private boolean energySavingMode;

    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        energySavingMode = !energySavingMode;

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        String modeEnabledMessage = messageProperties.getProperty("energy.saving.mode.enabled");
        String modeDisabledMessage = messageProperties.getProperty("energy.saving.mode.disabled");

        String statusMessage = energySavingMode ? modeEnabledMessage : modeDisabledMessage;

        HabboScanner.getInstance().sendPrivateMessage(userId, statusMessage);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.energy_saving.command.description");
    }
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
    public boolean getEnergySavingMode() {
        return energySavingMode;
    }

    public void setEnergySavingMode(boolean energySavingMode) {
        this.energySavingMode = energySavingMode;
    }
}

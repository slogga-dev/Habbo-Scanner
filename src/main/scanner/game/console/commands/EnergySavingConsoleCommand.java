package scanner.game.console.commands;

import scanner.game.console.ConsoleCommand;
import gearth.protocol.HMessage;
import scanner.HabboScanner;

import java.util.Properties;

public class EnergySavingConsoleCommand implements ConsoleCommand {
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

    public boolean getEnergySavingMode() {
        return energySavingMode;
    }

    public void setEnergySavingMode(boolean energySavingMode) {
        this.energySavingMode = energySavingMode;
    }
}

package org.slogga.habboscanner.logic.discord.commands.commands;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.common.InfoCommand;

public class InfoDiscordCommand extends InfoCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        super.execute(properties);
    }

    @Override
    protected void printStatus(CommandExecutorProperties properties, String variableName, boolean isActive) {
        String variableStatusMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message").getProperty("variable.status." +
                        (isActive ? "enabled": "disabled") + ".message");

        String message = variableName + variableStatusMessage;

        properties.getEvent().reply(message).queue();
    }
}

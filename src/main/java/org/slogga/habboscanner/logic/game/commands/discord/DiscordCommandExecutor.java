package org.slogga.habboscanner.logic.game.commands.discord;

import java.util.Properties;

import java.util.function.Supplier;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.LogoutCommand;
import org.slogga.habboscanner.models.CommandKeys;

public class DiscordCommandExecutor extends CommandExecutor {
    private final Properties commandDescriptionProperties;

    public DiscordCommandExecutor(CommandExecutorProperties properties) {
        super(properties);

        commandDescriptionProperties = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("command_description");

        setupCommands();
    }

    @Override
    public void setupCommands() {
//        setupCommand(CommandKeys.START);
//        setupCommand(CommandKeys.PAUSE);
//        setupCommand(CommandKeys.FOLLOW, option("mode", OptionType.STRING));
//        setupCommand(CommandKeys.RESUME);
//        setupCommand(CommandKeys.INFO);
//        setupCommand(CommandKeys.CONVERT, option("file", OptionType.STRING));
//        setupCommand(CommandKeys.UPDATE);
//        setupCommand(CommandKeys.ENERGY_SAVING);
//        setupCommand(CommandKeys.MAKESAY, option("text", OptionType.STRING));
        setupCommand(CommandKeys.LOGOUT, LogoutCommand::new);
//        setupCommand(CommandKeys.AUCTION);
//        setupCommand(CommandKeys.INFO_FROM_ID, option("id", OptionType.INTEGER),
//                option("type", OptionType.STRING));
    }

    private void setupCommand(CommandKeys commandKey, Supplier<Command> commandSupplier, OptionData... options) {
        String commandName = commandKey.getKey();
        String description = commandDescriptionProperties.getProperty("discord." + commandName + ".command.description");

        // Create a new command instance
        Command command = commandSupplier.get();

        commands.put(commandName, command);

        HabboScanner.getInstance().getDiscordBot().getDiscordAPI()
                .upsertCommand(commandName, description)
                .setGuildOnly(true).addOptions(options).queue();
    }

    private OptionData option(String optionName, OptionType optionType) {
        String description = commandDescriptionProperties.getProperty("discord." + optionName + ".option.description");

        return new OptionData(optionType, optionName, description, true);
    }
}


package org.slogga.habboscanner.logic.discord.commands;

import java.util.function.Supplier;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.discord.commands.commands.ConvertDiscordCommand;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.discord.commands.commands.*;

import org.slogga.habboscanner.models.CommandKeys;

public class DiscordCommandExecutor extends CommandExecutor {
    public DiscordCommandExecutor(CommandExecutorProperties properties) {
        super(properties);

        setupCommands();
    }

    public DiscordCommandExecutor(){
        super();
    }

    @Override
    public void setupCommands() {
        setupCommand(CommandKeys.START, StartDiscordCommand::new);
        setupCommand(CommandKeys.PAUSE, PauseDiscordCommand::new);
        setupCommand(CommandKeys.FOLLOW, FollowDiscordCommand::new, option("mode", OptionType.STRING));
        setupCommand(CommandKeys.RESUME, ResumeDiscordCommand::new);
        setupCommand(CommandKeys.INFO, InfoDiscordCommand::new);
        setupCommand(CommandKeys.CONVERT, ConvertDiscordCommand::new, option("file", OptionType.STRING));
        setupCommand(CommandKeys.UPDATE, UpdateDiscordCommand::new);
        setupCommand(CommandKeys.ENERGY_SAVING, EnergySavingDiscordCommand::new);
        setupCommand(CommandKeys.SHUTDOWN, ShutdownDiscordCommand::new);
        setupCommand(CommandKeys.AUCTION, AuctionDiscordCommand::new);
        setupCommand(CommandKeys.INFO_FROM_ID, InfoFromIdDiscordCommand::new, option("id", OptionType.INTEGER),
                option("type", OptionType.STRING));
    }

    private void setupCommand(CommandKeys commandKey, Supplier<Command> commandSupplier, OptionData... options) {
        String commandName = commandKey.getKey();
        String description = HabboScanner.getInstance().getConfigurator().getProperties()
                .get("command_description").getProperty(commandName + ".command.description");

        Command command = commandSupplier.get();

        commands.put(commandName, command);

        HabboScanner.getInstance().getDiscordBot().getDiscordAPI()
                .upsertCommand(commandName, description)
                .setGuildOnly(true).addOptions(options)
                .queue();
    }

    private OptionData option(String optionName, OptionType optionType) {
        String description = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("command_description")
                .getProperty("discord." + optionName + ".option.description");

        return new OptionData(optionType, optionName, description, true);
    }
}


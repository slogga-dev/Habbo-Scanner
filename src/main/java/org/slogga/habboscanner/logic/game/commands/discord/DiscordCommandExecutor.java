package org.slogga.habboscanner.logic.game.commands.discord;

import java.util.Properties;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.models.CommandKeys;

public class DiscordCommandExecutor extends CommandExecutor {
    public DiscordCommandExecutor(CommandExecutorProperties properties) {
        super(properties);

        setupCommands();
    }

    @Override
    public void setupCommands() {
        Properties commandDescriptionProperties = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("command_description");

        setupCommand(CommandKeys.START.getKey(), commandDescriptionProperties.getProperty("discord.start.command.description"));
        setupCommand(CommandKeys.PAUSE.getKey(), commandDescriptionProperties.getProperty("discord.pause.command.description"));
        setupCommand(CommandKeys.FOLLOW.getKey(), commandDescriptionProperties.getProperty("discord.follow.command.description"),
                new OptionData(OptionType.STRING, "mode", commandDescriptionProperties.getProperty("discord.mode.option.description"), true));
        setupCommand(CommandKeys.FOLLOW.getKey(), commandDescriptionProperties.getProperty("discord.resume.command.description"));
        setupCommand(CommandKeys.INFO.getKey(), commandDescriptionProperties.getProperty("discord.info.command.description"));
        setupCommand(CommandKeys.CONVERT.getKey(), commandDescriptionProperties.getProperty("discord.convert.command.description"),
                new OptionData(OptionType.STRING, "file", commandDescriptionProperties.getProperty("discord.file.option.description"), true));
        setupCommand(CommandKeys.UPDATE.getKey(), commandDescriptionProperties.getProperty("discord.update.command.description"));
        setupCommand(CommandKeys.ENERGY_SAVING.getKey(), commandDescriptionProperties.getProperty("discord.energy_saving.command.description"));
        setupCommand(CommandKeys.ENERGY_SAVING.getKey(), commandDescriptionProperties.getProperty("discord.makesay.command.description"),
                new OptionData(OptionType.STRING, "text", commandDescriptionProperties.getProperty("discord.text.option.description"), true));
        setupCommand(CommandKeys.LOGOUT.getKey(), commandDescriptionProperties.getProperty("discord.logout.command.description"));
        setupCommand(CommandKeys.AUCTION.getKey(), commandDescriptionProperties.getProperty("discord.auction.command.description"));
        setupCommand(CommandKeys.INFO_FROM_ID.getKey(), commandDescriptionProperties.getProperty("discord.info_from_id.command.description"),
                new OptionData(OptionType.INTEGER, "id", commandDescriptionProperties.getProperty("discord.id.option.description"), true),
                new OptionData(OptionType.STRING, "type", commandDescriptionProperties.getProperty("discord.type.option.description"), true));

//        HabboScanner.getInstance().getDiscordBot().getDiscordAPI()
//                .retrieveCommands().queue(this::storeCommandsInMap);
    }

    private void setupCommand(String commandName, String description, OptionData... options) {
//        CommandData commandData = new CommandData(commandName, description).setGuildOnly(true);
//
//        for (OptionData option : options) {
//            commandData.addOptions(option);
//        }
//
//        HabboScanner.getInstance().getDiscordBot().getDiscordAPI()
//                .upsertCommand(commandData).queue();
    }
}


package org.slogga.habboscanner.logic.discord.commands.commands;

import java.util.Objects;

import java.util.Properties;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.logic.discord.DiscordBot;

import org.slogga.habboscanner.logic.commands.*;
import org.slogga.habboscanner.logic.commands.common.follow.FollowCommand;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.FollowingAction;
import org.slogga.habboscanner.models.enums.SourceType;

public class FollowDiscordCommand extends FollowCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        SlashCommandInteractionEvent event = properties.getEvent();
        String mode = Objects.requireNonNull(event.getOption("mode")).getAsString();

        Properties messageProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("message");

        if (!mode.equals("furni_info") && !mode.equals("auction")) {
            String invalidModeMessage = messageProperties.getProperty("invalid.mode.message");

            event.reply(invalidModeMessage).setEphemeral(true).queue();

            return;
        }

        StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance.
                getCommands().get(CommandKeys.START.getKey());

        if (!startConsoleCommand.isBotRunning()) {
            String botNotActiveMessage = messageProperties.getProperty("bot.not.active.message");

            event.reply(botNotActiveMessage).queue();

            return;
        }

        sourceType = SourceType.DISCORD;
        followingAction = FollowingAction.fromValue(mode);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        if (discordBot == null) return;

        sendBotFollowingMessage(event);

        String discordUserId = event.getUser().getId();
        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        properties.setUserId(habboUserId);

        super.execute(properties);
    }

    private void sendBotFollowingMessage(SlashCommandInteractionEvent event) {
        String botMessageFollowing = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message").getProperty("bot.following.message");

        String[] botMessageFollowingArray = botMessageFollowing.split("---");

        int randomIndex = (int) (Math.random() * botMessageFollowingArray.length);
        botMessageFollowing = botMessageFollowingArray[randomIndex];

        event.reply(botMessageFollowing).queue();
    }
}

package org.slogga.habboscanner.logic.discord.commands.commands;

import java.io.IOException;

import java.sql.*;

import java.util.*;
import java.util.Date;

import org.apache.commons.lang3.tuple.Triple;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.discord.DiscordBot;

import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;

import org.slogga.habboscanner.logic.game.commands.*;

import org.slogga.habboscanner.models.ItemTimeline;
import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;

import org.slogga.habboscanner.utils.DateUtils;

public class InfoFromIdDiscordCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        SlashCommandInteractionEvent event = properties.getEvent();

        int id = validateId(event);

        if (id == -1) return;

        String type = validateType(event);

        if (type == null) return;

        FurnitypeEnum furnitypeEnum = FurnitypeEnum.fromValue(type);

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries = getClosestEntries(furnitypeEnum, id);
        Date estimatedDate = DateUtils.getLinearInterpolatedDate(closestEntries);

        if (!validateEstimatedDate(estimatedDate, event)) return;

        Timestamp timestamp = new Timestamp(estimatedDate.getTime());
        String formattedDate = DateUtils.formatTimestampToDate(timestamp);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();
        String discordUserId = event.getUser().getId();
        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        HabboScanner.getInstance().getConfigurator().getFurniMovementHandlers().getFurniHistoricalInfoBroadcaster()
                .broadcastFurniHistoryDetails(id, furnitypeEnum, formattedDate, habboUserId);

        StringBuilder aggregatedMessage = HabboScanner.getInstance().getConfigurator()
                .getFurniMovementHandlers().getFurniHistoricalInfoBroadcaster().getAggregatedMessage();

        event.reply(aggregatedMessage.toString()).queue();
    }

    private int validateId(SlashCommandInteractionEvent event) {
        int id = Objects.requireNonNull(event.getOption("id")).getAsInt();

        if (id <= 0) {
            replyToEvent(event, "no.id.entered.message");

            return -1;
        }

        return id;
    }

    private String validateType(SlashCommandInteractionEvent event) {
        String type = Objects.requireNonNull(event.getOption("type")).getAsString();

        if (!type.equals(FurnitypeEnum.FLOOR.getType().toLowerCase()) &&
                !type.equals(FurnitypeEnum.WALL.getType().toLowerCase())) {
            replyToEvent(event, "no.type.entered.message", true);

            return null;
        }

        return type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
    }

    private Triple<Integer, ItemTimeline, ItemTimeline> getClosestEntries(FurnitypeEnum furnitypeEnum, int id) {
        try {
            return ItemsTimelineDAO.selectClosestEntries(furnitypeEnum.getType(), id);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private boolean validateEstimatedDate(Date estimatedDate, SlashCommandInteractionEvent event) {
        if (String.valueOf(estimatedDate).equals("1970-01-01") || estimatedDate == null) {
            replyToEvent(event, "timeline.error.message");

            return false;
        }

        return true;
    }

    private void replyToEvent(SlashCommandInteractionEvent event, String messageKey) {
        replyToEvent(event, messageKey, false);
    }

    private void replyToEvent(SlashCommandInteractionEvent event, String messageKey, boolean isEphemeral) {
        String message = HabboScanner.getInstance().getConfigurator().getProperties()
                .get("message").getProperty(messageKey);

        if (isEphemeral) {
            event.reply(message).setEphemeral(true).queue();

            return;
        }

        event.reply(message).queue();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("info_from_id.command.description");
    }
}
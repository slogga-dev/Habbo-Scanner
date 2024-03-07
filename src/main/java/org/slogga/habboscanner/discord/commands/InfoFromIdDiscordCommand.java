package org.slogga.habboscanner.discord.commands;

import java.io.IOException;

import java.sql.*;

import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.slogga.habboscanner.discord.DiscordBot;
import org.apache.commons.lang3.tuple.Triple;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.discord.DiscordCommand;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;

import org.slogga.habboscanner.models.ItemTimeline;
import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;

import org.slogga.habboscanner.utils.DateUtils;

public class InfoFromIdDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int id = Objects.requireNonNull(event.getOption("id")).getAsInt();

        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message");

        if (id <= 0) {
            String noIdEnteredMessage = messageProperties.getProperty("no.id.entered.message");

            event.reply(noIdEnteredMessage).queue();

            return;
        }

        String type = Objects.requireNonNull(event.getOption("type")).getAsString();

        if (!type.equals(FurnitypeEnum.FLOOR.getType().toLowerCase()) &&
                !type.equals(FurnitypeEnum.WALL.getType().toLowerCase())) {
            String noTypeEnteredMessage = messageProperties.getProperty("no.type.entered.message");

            event.reply(noTypeEnteredMessage).setEphemeral(true).queue();

            return;
        }

        type = type.substring(0, 1).toUpperCase() +
                type.substring(1).toLowerCase();

        FurnitypeEnum furnitypeEnum = FurnitypeEnum.fromValue(type);

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries;

        try {
            closestEntries = ItemsTimelineDAO.selectClosestEntries(furnitypeEnum.getType(), id);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        Date estimatedDate = DateUtils.getLinearInterpolatedDate(closestEntries);

        if (String.valueOf(estimatedDate).equals("1970-01-01")) {
            String timelineErrorMessage = messageProperties.getProperty("timeline.error.message");

            event.reply(timelineErrorMessage).queue();

            return;
        }

        if (estimatedDate == null) return;

        Timestamp timestamp = new Timestamp(estimatedDate.getTime());
        String formattedDate = DateUtils.formatTimestampToDate(timestamp);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();
        String discordUserId = event.getUser().getId();

        int habboUserId = discordBot.getHabboIdFromDiscordId(discordUserId);

        HabboScanner.getInstance().getConfigurator().getFurniMovementHandlers().getFurniInfoProvider()
                .provideFurniInfo(id, furnitypeEnum, formattedDate, habboUserId);

        String furniInfoMessageSent = messageProperties.getProperty("furni.info.message.sent");

        event.reply(furniInfoMessageSent).queue();
    }
}
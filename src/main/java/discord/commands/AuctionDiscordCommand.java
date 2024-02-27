package discord.commands;

import java.io.IOException;

import java.sql.*;

import java.util.Date;
import java.util.Properties;

import discord.DiscordBot;
import org.apache.commons.lang3.tuple.Triple;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import discord.DiscordCommand;

import handlers.ItemProcessingHandlers;

import scanner.HabboScanner;

import models.ItemTimeline;
import models.furnitype.FurnitypeEnum;

import utils.DateUtils;

import database.dao.items.ItemsTimelineDAO;

public class AuctionDiscordCommand implements DiscordCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        ItemProcessingHandlers itemProcessingHandlers = HabboScanner.getInstance().getItemProcessingHandlers();

        int id = itemProcessingHandlers.getLastFurniPlacedId();

        Properties messageProperties = HabboScanner.getInstance().getMessageProperties();

        if (id <= 0) {
            String noFurniPlacedMessage = messageProperties.getProperty("no.furni.placed.message");

            event.reply(noFurniPlacedMessage).queue();

            return;
        }

        FurnitypeEnum type = itemProcessingHandlers.getLastFurniPlacedType();

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries;

        try {
            closestEntries = ItemsTimelineDAO.selectClosestEntries(type.getType(), id);
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

        HabboScanner.getInstance().getFurniMovementHandlers().getFurniInfoProvider()
                .provideFurniInfo(id, type, formattedDate, habboUserId);

        String newFurniMessageSent = messageProperties.getProperty("new.furni.message.sent");

        event.reply(newFurniMessageSent).queue();
    }
}
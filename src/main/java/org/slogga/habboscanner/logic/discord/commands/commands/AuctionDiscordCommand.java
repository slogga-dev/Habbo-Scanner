package org.slogga.habboscanner.logic.discord.commands.commands;

import java.io.IOException;

import java.sql.*;

import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.discord.DiscordBot;

import org.slogga.habboscanner.handlers.item.ItemAdditionHandlers;

import org.slogga.habboscanner.logic.commands.*;

import org.slogga.habboscanner.models.furni.ItemTimeline;
import org.slogga.habboscanner.models.enums.FurnitypeEnum;

import org.slogga.habboscanner.utils.DateUtils;

import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;

public class AuctionDiscordCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        SlashCommandInteractionEvent event = properties.getEvent();

        ItemAdditionHandlers itemAdditionHandlers = HabboScanner.getInstance()
                .getConfigurator().getItemAdditionHandlers();

        int id = itemAdditionHandlers.getLastFurniPlacedId();

        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message");

        if (id <= 0) {
            String noFurniPlacedMessage = messageProperties.getProperty("no.furni.placed.message");

            event.reply(noFurniPlacedMessage).queue();

            return;
        }

        FurnitypeEnum type = itemAdditionHandlers.getLastFurniPlacedType();

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

        HabboScanner.getInstance().getConfigurator().getFurniMovementHandlers().getFurniHistoricalInfoBroadcaster()
                .broadcastFurniHistoryDetails(id, type, formattedDate, habboUserId);

        StringBuilder aggregatedMessage = HabboScanner.getInstance().getConfigurator()
                .getFurniMovementHandlers().getFurniHistoricalInfoBroadcaster().getAggregatedMessage();

        event.reply(aggregatedMessage.toString()).queue();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("auction.command.description");
    }
}
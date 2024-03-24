package org.slogga.habboscanner.logic.discord;

import java.util.Random;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.slogga.habboscanner.HabboScanner;

public class DiscordMessageHandler {
    private final JDA discordAPI;

    public DiscordMessageHandler(JDA discordAPI) {
        this.discordAPI = discordAPI;
    }

    public void sendMessageToFeedChannel(String message) {
        String feedChannelId = HabboScanner.getInstance().getConfigurator().getProperties().get("discord")
                .getProperty("discord.bot.feed.channel.id");

        TextChannel channel = discordAPI.getTextChannelById(feedChannelId);

        if (channel == null) return;

        channel.sendMessage(message).queue();
    }

    public void sendRandomPhrase(MessageReceivedEvent event) {
        Random random = new Random();

        int responseInterval = Integer.parseInt(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("discord").getProperty("discord.bot.random.phrases.response.interval"));

        int randomMessageSendThreshold = random.nextInt(responseInterval) + 1;

        String phrasesList = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("discord").getProperty("discord.bot.random.phrases.list");
        String[] phrases = phrasesList.split("---");

        int phraseIndex = random.nextInt(phrases.length);

        String message = phrases[phraseIndex];

        if (randomMessageSendThreshold < 290) return;

        event.getChannel().sendMessage(message).queue();
    }
}
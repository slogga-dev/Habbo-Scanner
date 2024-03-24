package org.slogga.habboscanner.discord;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.requests.GatewayIntent;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.*;

public class DiscordBot extends ListenerAdapter {
    @Getter
    private JDA discordAPI;

    private Set<String> authorizedUserIds = new HashSet<>();

    public DiscordBot()  {
        refreshAuthorizedUserList();
        initializeDiscordAPI();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        setCommandExecutorProperties(event);

        Command command = CommandFactory.commandExecutorInstance.getCommands().get(commandName);

        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message");

        if (command == null) {
            String unknownCommandMessage = messageProperties.getProperty("unknown.command.message");

            event.reply(unknownCommandMessage).setEphemeral(true).queue();

            return;
        }

        if (!authorizedUserIds.contains(event.getUser().getId())) {
            String noPermissionMessage = messageProperties.getProperty("no.permission.message");

            event.reply(noPermissionMessage).setEphemeral(true).queue();

            return;
        }

        command.execute(CommandFactory.commandExecutorInstance.getProperties());
    }

    private void setCommandExecutorProperties(SlashCommandInteractionEvent event){
        CommandExecutorProperties commandExecutorProperties = new CommandExecutorProperties();

        commandExecutorProperties.setEvent(event);

        CommandFactory.getCommandExecutor(CommandExecutorType.DISCORD, commandExecutorProperties);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        boolean discordBotAllowRandomPhrases = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator().getProperties().get("discord").getProperty("discord.bot.allow.random.phrases"));

        if (event.getAuthor().isBot() ||
                !authorizedUserIds.contains(event.getAuthor().getId()) ||
                !discordBotAllowRandomPhrases) return;

        sendRandomPhrase(event);
    }

    public int getHabboIdFromDiscordId(String discordId){
        String discordToHabboIdMapping = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties().get("discord")
                .getProperty("discord.to.habbo.id.mapping");

        return Arrays.stream(discordToHabboIdMapping.split(";"))
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(entries -> entries[0], idPair -> Integer.parseInt(idPair[1])))
                .get(discordId);
    }

    public void sendMessageToFeedChannel(String message) {
        String feedChannelId = HabboScanner.getInstance().getConfigurator().getProperties().get("discord")
                .getProperty("discord.bot.feed.channel.id");

        TextChannel channel = discordAPI.getTextChannelById(feedChannelId);

        if (channel == null) return;

        channel.sendMessage(message).queue();
    }

    public void updateActivity(String activity) {
        discordAPI.getPresence().setActivity(Activity.playing(activity));
    }

    private void refreshAuthorizedUserList() {
        String discordToHabboIdMapping = HabboScanner.getInstance().getConfigurator().getProperties().get("discord").getProperty("discord.to.habbo.id.mapping");
        Set<String> updatedAuthorizedDiscordIds = new HashSet<>();

        String[] discordToHabboIdEntries = discordToHabboIdMapping.split(";");

        for (String entry : discordToHabboIdEntries) {
            String[] idPair = entry.split(":");
            String discordId = idPair[0];

            updatedAuthorizedDiscordIds.add(discordId);
        }

        authorizedUserIds = updatedAuthorizedDiscordIds;
    }

    private void initializeDiscordAPI() {
        String token = HabboScanner.getInstance().getConfigurator().getProperties().get("discord")
                .getProperty("discord.bot.token");

        String readyMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("discord.bot.ready.message");

        discordAPI = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing(readyMessage))
                .addEventListeners(this)
                .build();
    }

    private void sendRandomPhrase(MessageReceivedEvent event) {
        Random random = new Random();

        int responseInterval = Integer.parseInt(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("discord").getProperty("discord.bot.random.phrases.response.interval"));

        int randomMessageSendThreshold = random.nextInt(responseInterval) + 1;

        String phrasesList =HabboScanner.getInstance()
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

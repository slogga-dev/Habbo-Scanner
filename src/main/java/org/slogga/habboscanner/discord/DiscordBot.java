package org.slogga.habboscanner.discord;

import java.util.*;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.slogga.habboscanner.discord.commands.*;

import org.slogga.habboscanner.HabboScanner;

public class DiscordBot extends ListenerAdapter {
    private JDA discordAPI;

    private final Map<String, IDiscordCommand> commands = new HashMap<>();

    private Set<String> authorizedUserIds = new HashSet<>();

    public DiscordBot()  {
        refreshAuthorizedUserList();
        initializeDiscordAPI();
        setupBotCommands();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        IDiscordCommand command = commands.get(commandName);

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

        command.execute(event);
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

    private void setupBotCommands() {
        setupStartCommand();
        setupPauseCommand();
        setupFollowCommand();
        setupResumeCommand();
        setupInfoCommand();
        setupConvertCommand();
        setupUpdateCommand();
        setupEnergySavingCommand();
        setupMakeSayCommand();
        setupLogoutCommand();
        setupAuctionCommand();
        setupFurniInfoFromIdCommand();

        discordAPI.retrieveCommands().queue(this::storeCommandsInMap);
    }

    private void setupStartCommand() {
        String description = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("discord.start.command.description");

        discordAPI.upsertCommand("start", description)
                .setGuildOnly(true).queue();
    }

    private void setupPauseCommand() {
        String description = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("discord.pause.command.description");

        discordAPI.upsertCommand("pause", description)
                .setGuildOnly(true).queue();
    }

    private void setupFollowCommand() {
        Properties commandDescriptionProperties = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("command_description");

        String description = commandDescriptionProperties.getProperty("discord.follow.command.description");

        String modeOptionDescription = commandDescriptionProperties.getProperty("discord.mode.option.description");

        discordAPI.upsertCommand("follow", description)
                .setGuildOnly(true)
                .addOption(OptionType.STRING, "mode", modeOptionDescription, true)
                .queue();
    }

    private void setupResumeCommand() {
        String description = HabboScanner
                .getInstance()
                .getConfigurator()
                .getProperties().get("command_description")
                .getProperty("discord.resume.command.description");

        discordAPI.upsertCommand("resume", description)
                .setGuildOnly(true).queue();
    }

    private void setupInfoCommand() {
        String description = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("discord.info.command.description");

        discordAPI.upsertCommand("info", description)
                .setGuildOnly(true).queue();
    }

    private void setupConvertCommand() {
        Properties commandDescriptionProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description");

        String description = commandDescriptionProperties.getProperty("discord.convert.command.description");

        String fileOptionDescription = commandDescriptionProperties.getProperty("discord.file.option.description");

        discordAPI.upsertCommand("convert", description)
                .addOption(OptionType.STRING, "file", fileOptionDescription, true)
                .queue();
    }

    private void setupUpdateCommand() {
        String description = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("discord.update.command.description");

        discordAPI.upsertCommand("update", description).setGuildOnly(true).queue();
    }

    private void setupEnergySavingCommand() {
        String description = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties().get("command_description")
                .getProperty("discord.energy_saving.command.description");

        discordAPI.upsertCommand("energy_saving", description)
                .setGuildOnly(true).queue();
    }

    private void setupMakeSayCommand() {
        Properties commandDescriptionProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties().get("command_description");

        String description = commandDescriptionProperties.getProperty("discord.makesay.command.description");

        String textOptionDescription = commandDescriptionProperties.getProperty("discord.text.option.description");

        discordAPI.upsertCommand("makesay", description)
                .setGuildOnly(true).addOption(OptionType.STRING,
                        "text", textOptionDescription).queue();
    }

    private void setupLogoutCommand() {
        String description = HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("discord.logout.command.description");

        discordAPI.upsertCommand("logout", description).setGuildOnly(true).queue();
    }

    private void setupAuctionCommand() {
        String description = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("command_description")
                .getProperty("discord.auction.command.description");

        discordAPI.upsertCommand("auction", description).setGuildOnly(true).queue();
    }

    private void setupFurniInfoFromIdCommand() {
        Properties commandDescriptionProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("command_description");

        String description = commandDescriptionProperties.getProperty("discord.info_from_id.command.description");

        String idOptionDescription = commandDescriptionProperties.getProperty("discord.id.option.description");
        String typeOptionDescription = commandDescriptionProperties.getProperty("discord.type.option.description");

        discordAPI.upsertCommand("info_from_id", description)
                .addOption(OptionType.INTEGER, "id", idOptionDescription, true)
                .addOption(OptionType.STRING, "type", typeOptionDescription, true)
                .setGuildOnly(true).queue();
    }

    private void storeCommandsInMap(List<Command> commandList) {
        for (Command command : commandList) {
            switch (command.getName()) {
                case "start":
                    commands.put(command.getName(), new StartDiscordCommand());
                    break;

                case "update":
                    commands.put(command.getName(), new UpdateDiscordCommand());
                    break;

                case "resume":
                    commands.put(command.getName(), new ResumeDiscordCommand());
                    break;

                case "pause":
                    commands.put(command.getName(), new PauseDiscordCommand());
                    break;

                case "follow":
                    commands.put(command.getName(), new FollowDiscordCommand());
                    break;

                case "logout":
                    commands.put(command.getName(), new LogoutDiscordCommand());
                    break;

                case "info":
                    commands.put(command.getName(), new InfoDiscordCommand());
                    break;

                case "makesay":
                    commands.put(command.getName(), new MakeSayCommand());
                    break;

                case "energy_saving":
                    commands.put(command.getName(), new EnergySavingDiscordCommand());
                    break;

                case "convert":
                    commands.put(command.getName(), new ConvertDiscordCommand());
                    break;

                case "auction":
                    commands.put(command.getName(), new AuctionDiscordCommand());
                    break;

                case "info_from_id":
                    commands.put(command.getName(), new InfoFromIdDiscordCommand());

                    break;

                default:
                    break;
            }
        }
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

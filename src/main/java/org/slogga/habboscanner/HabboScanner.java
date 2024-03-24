package org.slogga.habboscanner;

import java.util.concurrent.*;

import gearth.extensions.*;

import lombok.*;

import org.slogga.habboscanner.logic.discord.DiscordBot;

import org.slogga.habboscanner.logic.configurators.*;

@Getter
@Setter
@ExtensionInfo(
        Title = "Habbo Scanner",
        Description = "Scan data all around Habbo!",
        Version = "3.0.0",
        Author = "slogga.it"
)
public class HabboScanner extends Extension {
    private static HabboScanner instance;

    private HabboScannerConfigurator configurator = new HabboScannerConfigurator();
    private FurnidataConfigurator furnidataConfigurator = new FurnidataConfigurator();

    private DiscordBot discordBot;

    private boolean criticalAirCrashWarning;

    public static void main(String[] args) {
        instance = new HabboScanner(args);
        instance.run();
    }

    public HabboScanner(String[] args) {
        super(args);
    }

    public static HabboScanner getInstance() {
        if (instance == null)
            throw new IllegalStateException("HabboScanner instance has not yet been initialized.");

        return instance;
    }

    @Override
    protected void initExtension() {
        configurator.setupConfig();
        furnidataConfigurator.setupConfig();

        boolean isDiscordBotEnabled = Boolean.parseBoolean(configurator.getProperties()
                .get("discord").getProperty("discord.bot.enabled"));
        boolean isBotEnabled = Boolean.parseBoolean(configurator.getProperties()
                .get("bot").getProperty("bot.enabled"));

//        CommandExecutorType currentType = CommandExecutorType.CONSOLE;

        if (isDiscordBotEnabled && isBotEnabled) {
            try {
                discordBot = new DiscordBot();
//                currentType = CommandExecutorType.DISCORD;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        scheduleAirCrashCheck();
    }

    @Override
    protected void onEndConnection() {
        if (discordBot != null) {
            String botCrashMessage = configurator
                    .getProperties()
                    .get("message")
                    .getProperty("bot.crash.message");

            discordBot.getMessageHandler().sendMessageToFeedChannel(botCrashMessage);
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }

    private void scheduleAirCrashCheck() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        /*
            Time in milliseconds.
            400000 ms is approximately 6.67 minutes (400000 ms / 60000 ms/minutes)
        */
        final long accessTimeout = 400000;
        final String crashMessage = configurator.getProperties().get("message")
                .getProperty("bot.room.manager.crash.message");

        long lastRoomAccess = configurator.getRoomEntryHandler().getLastRoomAccess();

//        CommandFactory.getCommandExecutor(currentType);

//        StartCommand startCommand = (StartCommand) CommandFactory.commandExecutorInstance
//                .getCommands()
//                .get(CommandKeys.START.getKey());

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            boolean isAccessRecent = lastRoomAccess > 0;
            boolean isTimeExceeded = (System.currentTimeMillis() - lastRoomAccess) > accessTimeout;

            if (criticalAirCrashWarning || !isAccessRecent /*|| !startCommand.isHasExecuted()*/ || !isTimeExceeded)
                return;

            if (discordBot != null)
                discordBot.getMessageHandler().sendMessageToFeedChannel(crashMessage);

            criticalAirCrashWarning = true;
        }, 0, 2, TimeUnit.MINUTES);
    }
}
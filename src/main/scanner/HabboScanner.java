package scanner;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import java.util.*;
import java.util.concurrent.*;

import com.google.gson.Gson;

import org.slf4j.*;

import scanner.discord.DiscordBot;

import scanner.game.console.ConsoleCommand;
import scanner.game.console.commands.EnergySavingConsoleCommand;
import scanner.game.console.commands.start.StartConsoleCommand;

import gearth.extensions.*;
import gearth.protocol.*;

import scanner.database.dao.items.ItemsDAO;

import scanner.furnidata.Furnidata;

import scanner.utils.JSONUtils;
import scanner.logic.configurators.HabboScannerConfigurator;

@ExtensionInfo(
        Title = "Habbo scanner",
        Description = "Scan data all around Habbo!",
        Version = "3.0.0",
        Author = "slogga.it"
)
public class HabboScanner extends Extension {
    private static HabboScanner instance;
    private static final Logger logger = LoggerFactory.getLogger(HabboScanner.class);
    private HabboScannerConfigurator configurator;
    private Map<String, Map<String, String>> items;
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
    public void moveToRoom(int roomId) {
        HPacket packet = new HPacket("GetGuestRoom", HMessage.Direction.TOSERVER,
                roomId, 0, 1);

        sendToServer(packet);
    }

    public void sendPrivateMessage(int userId, String text) {
        HPacket packet = new HPacket("SendMsg", HMessage.Direction.TOSERVER,
                userId, text);

        sendToServer(packet);
    }

    public void sendNavigatorSearch(String searchType, String searchValue) {
        HPacket packet = new HPacket("NewNavigatorSearch",
                HMessage.Direction.TOSERVER, searchType, searchValue);

        sendToServer(packet);
    }

    public void followUser(int userId) {
        HPacket packet = new HPacket("FollowFriend",
                HMessage.Direction.TOSERVER, userId);

        sendToServer(packet);
    }

    public void sendAvatarExpression(int expressionId) {
        HPacket packet = new HPacket("AvatarExpression",
                HMessage.Direction.TOSERVER, expressionId);

        sendToServer(packet);
    }

    public void goToHotelView() {
        HPacket packet = new HPacket("Quit", HMessage.Direction.TOSERVER,
                1);

        sendToServer(packet);
    }

    public void dance(int danceId) {
        HPacket packet = new HPacket("Dance", HMessage.Direction.TOSERVER, danceId);

        sendToServer(packet);
    }

    public void sign(int signId) {
        HPacket packet = new HPacket("Sign", HMessage.Direction.TOSERVER, signId);

        sendToServer(packet);
    }
    public Map<String, Map<String, String>> getItems() {
        return items;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public boolean getCriticalAirCrashWarning() {
        return criticalAirCrashWarning;
    }

    public HabboScannerConfigurator getConfigurator() {
        return configurator;
    }

    public void setItems(Map<String, Map<String, String>> items) {
        this.items = items;
    }

    public void setCriticalAirCrashWarning(boolean criticalAirCrashWarning) {
        this.criticalAirCrashWarning = criticalAirCrashWarning;
    }

    @Override
    protected void initExtension() {
        configurator = new HabboScannerConfigurator();
        configurator.setupConfig();

        try {
            fetchFurnidata();

            items = ItemsDAO.fetchItems();
        } catch (IOException | InterruptedException | SQLException exception) {
            throw new RuntimeException(exception);
        }

        boolean isDiscordBotEnabled = Boolean.parseBoolean(configurator.getProperties().get("discord").getProperty("discord.bot.enabled"));
        boolean isBotEnabled = Boolean.parseBoolean(configurator.getProperties().get("bot").getProperty("bot.enabled"));

        if (isDiscordBotEnabled && isBotEnabled) {
            try {
                discordBot = new DiscordBot();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        scheduleAirCrashCheck();

        registerHandlers();
    }

    @Override
    protected void onEndConnection() {
        if (discordBot != null) {
            String botCrashMessage = configurator
                    .getProperties()
                    .get("message")
                    .getProperty("bot.crash.message");

            discordBot.sendMessageToFeedChannel(botCrashMessage);
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }





    private void fetchFurnidata() throws InterruptedException, IOException {
        String hotelDomain = configurator
                .getProperties()
                .get("bot")
                .getProperty("hotel.domain");

        List<String> validDomains = Arrays.asList("s2", "it", "fi", "es", "de",
                "com.br", "com.tr", "com", "fr", "nl");

        if (!validDomains.contains(hotelDomain)) {
            logger.error("The hotel domain is incorrect.");

            System.exit(0);
        }

        String furnidataURL = getFurnidataURL(hotelDomain);
        String furnidataJSON = JSONUtils.fetchJSON(furnidataURL);

        Gson gson = new Gson();

        Furnidata furnidata = gson.fromJson(furnidataJSON, Furnidata.class);
        Furnidata.setInstance(furnidata);
    }

    private void scheduleAirCrashCheck() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        final long accessTimeout = 400000; // Time in milliseconds. 400000 ms is approximately 6.67 minutes (400000 ms / 60000 ms/minute)
        final String crashMessage = configurator.getProperties().get("message").getProperty("bot.room.manager.crash.message");

        long lastRoomAccess = configurator.getRoomInfoHandlers().getLastRoomAccess();
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) configurator.getConsoleHandlers().getCommands().get(":start");

        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            boolean isAccessRecent = lastRoomAccess > 0;
            boolean isTimeExceeded = (System.currentTimeMillis() - lastRoomAccess) > accessTimeout;

            if (criticalAirCrashWarning || !isAccessRecent || !isBotRunning || !isTimeExceeded)
                return;

            if (discordBot != null)
                discordBot.sendMessageToFeedChannel(crashMessage);

            criticalAirCrashWarning = true;
        }, 0, 2, TimeUnit.MINUTES);
    }

    private void registerHandlers() {
        intercept(HMessage.Direction.TOCLIENT, "RoomReady", configurator.getRoomInfoHandlers()::onRoomReady);
        intercept(HMessage.Direction.TOCLIENT, "GetGuestRoomResult", configurator.getRoomInfoHandlers()::onGetGuestRoomResult);
        intercept(HMessage.Direction.TOCLIENT, "RoomVisualizationSettings", configurator.getRoomInfoHandlers()::onRoomVisualizationSettings);

        intercept(HMessage.Direction.TOCLIENT, "Objects", configurator.getItemProcessingHandlers()::onFloorItems);
        intercept(HMessage.Direction.TOCLIENT, "Items", configurator.getItemProcessingHandlers()::onWallItems);
        intercept(HMessage.Direction.TOCLIENT, "ObjectAdd", configurator.getItemProcessingHandlers()::onObjectAdd);
        intercept(HMessage.Direction.TOCLIENT, "ItemAdd", configurator.getItemProcessingHandlers()::onItemAdd);

        intercept(HMessage.Direction.TOSERVER, "MoveObject", configurator.getFurniMovementHandlers()::onMoveFurni);
        intercept(HMessage.Direction.TOCLIENT, "ObjectUpdate", configurator.getFurniMovementHandlers()::onMoveFurni);
        intercept(HMessage.Direction.TOSERVER, "MoveWallItem", configurator.getFurniMovementHandlers()::onMoveWallItem);
        intercept(HMessage.Direction.TOCLIENT, "ItemUpdate", configurator.getFurniMovementHandlers()::onMoveWallItem);

        intercept(HMessage.Direction.TOCLIENT, "NavigatorSearchResultBlocks", configurator.getNavigatorHandlers()::onNavigatorSearchResultBlocks);

        intercept(HMessage.Direction.TOCLIENT, "NewConsole", configurator.getConsoleHandlers()::onNewConsole);
        intercept(HMessage.Direction.TOSERVER, "SendMsg", configurator.getConsoleHandlers()::onNewConsole);

        intercept(HMessage.Direction.TOCLIENT, "Users", configurator.getUserHandlers()::onUsers);
        intercept(HMessage.Direction.TOCLIENT, "Chat", configurator.getUserHandlers()::onChat);

        intercept(HMessage.Direction.TOCLIENT, "CantConnect", configurator.getErrorHandlers()::onCantConnect);
        intercept(HMessage.Direction.TOCLIENT, "ErrorReport", configurator.getErrorHandlers()::onErrorReport);

        intercept(HMessage.Direction.TOCLIENT, "Users", this::onClientOptimization);
        intercept(HMessage.Direction.TOCLIENT, "RoomProperty", this::onClientOptimization);
        intercept(HMessage.Direction.TOCLIENT, "HeightMap", this::onClientOptimization);
    }

    private String getFurnidataURL(String hotelDomain) {
        if (hotelDomain.equals("s2"))
            return "https://sandbox.habbo.com/gamedata/furnidata_json/1";

        return "https://www.habbo." + hotelDomain + "/gamedata/furnidata_json/1";
    }

    private void onClientOptimization(HMessage message) {
        Map<String, ConsoleCommand> commands = configurator.getConsoleHandlers().getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(":energy_saving");
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(":start");
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        if (!energySavingMode || !isBotRunning) return;

        message.setBlocked(true);
    }
}
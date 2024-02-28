package scanner;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import java.util.*;
import java.util.concurrent.*;

import com.google.gson.Gson;

import org.slf4j.*;

import discord.DiscordBot;

import game.console.ConsoleCommand;
import game.console.commands.EnergySavingConsoleCommand;
import game.console.commands.start.StartConsoleCommand;

import gearth.extensions.*;
import gearth.protocol.*;

import database.dao.items.ItemsDAO;

import furnidata.Furnidata;

import handlers.*;

import utils.JSONUtils;

@ExtensionInfo(
        Title = "Habbo scanner",
        Description = "Scan data all around Habbo!",
        Version = "3.0.0",
        Author = "slogga.it"
)
public class HabboScanner extends Extension {
    private static final Logger logger = LoggerFactory.getLogger(HabboScanner.class);

    private static HabboScanner instance;

    public static HabboScanner getInstance() {
        if (instance == null)
            throw new IllegalStateException("HabboScanner instance has not yet been initialized.");

        return instance;
    }

    public static void main(String[] args) {
        instance = new HabboScanner(args);
        instance.run();
    }

    private final Properties botProperties = new Properties();
    private final Properties messageProperties = new Properties();
    private final Properties discordProperties = new Properties();
    private final Properties commandDescriptionProperties = new Properties();

    private final RoomInfoHandlers roomInfoHandlers = new RoomInfoHandlers();
    private final ItemProcessingHandlers itemProcessingHandlers = new ItemProcessingHandlers();
    private final FurniMovementHandlers furniMovementHandlers = new FurniMovementHandlers();
    private final NavigatorHandlers navigatorHandlers = new NavigatorHandlers();
    private final ConsoleHandlers consoleHandlers = new ConsoleHandlers();
    private final UserHandlers userHandlers = new UserHandlers();
    private final ErrorHandlers errorHandlers = new ErrorHandlers();

    private Map<String, Map<String, String>> items;

    private DiscordBot discordBot;

    private boolean criticalAirCrashWarning;

    public HabboScanner(String[] args) {
        super(args);
    }

    @Override
    protected void initExtension() {
        loadBotProperties();
        loadMessageProperties();
        loadDiscordBotProperties();
        loadCommandDescriptionProperties();

        try {
            fetchFurnidata();

            items = ItemsDAO.fetchItems();
        } catch (IOException | InterruptedException | SQLException exception) {
            throw new RuntimeException(exception);
        }

        boolean isDiscordBotEnabled = Boolean.parseBoolean(discordProperties.getProperty("discord.bot.enabled"));
        boolean isBotEnabled = Boolean.parseBoolean(botProperties.getProperty("bot.enabled"));

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
            String botCrashMessage = HabboScanner.getInstance().getMessageProperties()
                    .getProperty("bot.crash.message");

            discordBot.sendMessageToFeedChannel(botCrashMessage);
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }

    public void loadBotProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("bot.properties")) {
            assert inputStream != null;

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                botProperties.load(streamReader);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Error reading BOT properties.", exception);
        }
    }

    public void whisperMessage(String message) {
        HPacket packet = new HPacket("Whisper", HMessage.Direction.TOCLIENT,
                -1, message, 0, 30, 0, -1);

        sendToClient(packet);
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

    public Properties getBotProperties() {
        return botProperties;
    }
    public Properties getMessageProperties() {
        return messageProperties;
    }
    public Properties getDiscordProperties() {
        return discordProperties;
    }
    public Properties getCommandDescriptionProperties() { return commandDescriptionProperties; }

    public RoomInfoHandlers getRoomInfoHandlers() {
        return roomInfoHandlers;
    }
    public ItemProcessingHandlers getItemProcessingHandlers() {
        return itemProcessingHandlers;
    }
    public ConsoleHandlers getConsoleHandlers() {
        return consoleHandlers;
    }
    public FurniMovementHandlers getFurniMovementHandlers() {
        return furniMovementHandlers;
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

    public void setItems(Map<String, Map<String, String>> items) {
        this.items = items;
    }

    public void setCriticalAirCrashWarning(boolean criticalAirCrashWarning) {
        this.criticalAirCrashWarning = criticalAirCrashWarning;
    }

    private void fetchFurnidata() throws InterruptedException, IOException {
        String hotelDomain = botProperties.getProperty("hotel.domain");

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
        final String crashMessage = "che palle, il Room Manager di AIR è crashato -.- @everyone dai riavviatemi dio";

        long lastRoomAccess = roomInfoHandlers.getLastRoomAccess();
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) consoleHandlers.getCommands().get(":start");

        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            boolean isAccessRecent = lastRoomAccess > 0;
            boolean isTimeExceeded = (System.currentTimeMillis() - lastRoomAccess) > accessTimeout;

            if (criticalAirCrashWarning || !isAccessRecent || !isBotRunning || !isTimeExceeded)
                return;

            discordBot.sendMessageToFeedChannel(crashMessage);

            criticalAirCrashWarning = true;
        }, 0, 2, TimeUnit.MINUTES);
    }

    private void registerHandlers() {
        intercept(HMessage.Direction.TOCLIENT, "RoomReady", roomInfoHandlers::onRoomReady);
        intercept(HMessage.Direction.TOCLIENT, "GetGuestRoomResult", roomInfoHandlers::onGetGuestRoomResult);
        intercept(HMessage.Direction.TOCLIENT, "RoomVisualizationSettings", roomInfoHandlers::onRoomVisualizationSettings);

        intercept(HMessage.Direction.TOCLIENT, "Objects", itemProcessingHandlers::onFloorItems);
        intercept(HMessage.Direction.TOCLIENT, "Items", itemProcessingHandlers::onWallItems);
        intercept(HMessage.Direction.TOCLIENT, "ObjectAdd", itemProcessingHandlers::onObjectAdd);
        intercept(HMessage.Direction.TOCLIENT, "ItemAdd", itemProcessingHandlers::onItemAdd);

        intercept(HMessage.Direction.TOSERVER, "MoveObject", furniMovementHandlers::onMoveFurni);
        intercept(HMessage.Direction.TOCLIENT, "ObjectUpdate", furniMovementHandlers::onMoveFurni);
        intercept(HMessage.Direction.TOSERVER, "MoveWallItem", furniMovementHandlers::onMoveWallItem);
        intercept(HMessage.Direction.TOCLIENT, "ItemUpdate", furniMovementHandlers::onMoveWallItem);

        intercept(HMessage.Direction.TOCLIENT, "NavigatorSearchResultBlocks", navigatorHandlers::onNavigatorSearchResultBlocks);

        intercept(HMessage.Direction.TOCLIENT, "NewConsole", consoleHandlers::onNewConsole);
        intercept(HMessage.Direction.TOSERVER, "SendMsg", consoleHandlers::onNewConsole);

        intercept(HMessage.Direction.TOCLIENT, "Users", userHandlers::onUsers);
        intercept(HMessage.Direction.TOCLIENT, "Chat", userHandlers::onChat);

        intercept(HMessage.Direction.TOCLIENT, "CantConnect", errorHandlers::onCantConnect);
        intercept(HMessage.Direction.TOCLIENT, "ErrorReport", errorHandlers::onErrorReport);

        intercept(HMessage.Direction.TOCLIENT, "Users", this::onClientOptimization);
        intercept(HMessage.Direction.TOCLIENT, "RoomProperty", this::onClientOptimization);
        intercept(HMessage.Direction.TOCLIENT, "HeightMap", this::onClientOptimization);
    }

    private void loadMessageProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("message.properties")) {
            assert inputStream != null;

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                messageProperties.load(streamReader);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Error reading message properties.", exception);
        }
    }

    private void loadDiscordBotProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("discord.properties")) {
            assert inputStream != null;

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                discordProperties.load(streamReader);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Error reading Discord BOT properties.", exception);
        }
    }

    private void loadCommandDescriptionProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("command_description.properties")) {
            assert inputStream != null;

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                commandDescriptionProperties.load(streamReader);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Error reading command description properties.", exception);
        }
    }

    private String getFurnidataURL(String hotelDomain) {
        if (hotelDomain.equals("s2"))
            return "https://sandbox.habbo.com/gamedata/furnidata_json/1";

        return "https://www.habbo." + hotelDomain + "/gamedata/furnidata_json/1";
    }

    private void onClientOptimization(HMessage message) {
        Map<String, ConsoleCommand> commands = consoleHandlers.getCommands();

        EnergySavingConsoleCommand energySavingConsoleCommand = (EnergySavingConsoleCommand) commands.get(":energy_saving");
        boolean energySavingMode = energySavingConsoleCommand.getEnergySavingMode();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(":start");
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        if (!energySavingMode || !isBotRunning) return;

        message.setBlocked(true);
    }
}
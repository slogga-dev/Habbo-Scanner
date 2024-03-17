package org.slogga.habboscanner.logic.configurators;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import gearth.protocol.HMessage;

import lombok.Data;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.handlers.*;
import org.slogga.habboscanner.handlers.item.*;
import org.slogga.habboscanner.handlers.room.*;
import org.slogga.habboscanner.logic.DefaultValues;

@Data
public class HabboScannerConfigurator implements IConfigurator {
    private Map<String, Properties> properties = new HashMap<>();

    private RoomEntryHandler roomEntryHandler;
    private RoomDetailsHandlers roomDetailsHandlers;
    private ItemPlacementHandlers itemPlacementHandlers;
    private ItemAdditionHandlers itemAdditionHandlers;
    private FurniMovementHandlers furniMovementHandlers;
    private NavigatorHandlers navigatorHandlers;
    private ConsoleHandlers consoleHandlers;
    private UserHandlers userHandlers;
    private ErrorHandlers errorHandlers;
    private ClientOptimizationHandler clientOptimizationHandler;

    @Override
    public void setupConfig() {
        setupProperties();
        setupHandlers();
        registerHandlers();
    }

    public void loadProperty(String name) {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(String.format("%s.properties", name))) {
            assert inputStream != null;

            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Properties value = new Properties();
                value.load(streamReader);
                properties.put(name, value);
            }
        } catch (IOException exception) {
            throw new RuntimeException(String.format("Error reading %s properties.", name), exception);
        }
    }

    private void setupProperties() {
        properties = new HashMap<>();

        DefaultValues.getInstance().getPropertyNames().forEach(this::loadProperty);
    }

    private void setupHandlers() {
        roomEntryHandler = new RoomEntryHandler();
        roomDetailsHandlers = new RoomDetailsHandlers();
        itemAdditionHandlers = new ItemAdditionHandlers();
        itemPlacementHandlers = new ItemPlacementHandlers();
        furniMovementHandlers = new FurniMovementHandlers();
        navigatorHandlers = new NavigatorHandlers();
        consoleHandlers = new ConsoleHandlers();
        userHandlers = new UserHandlers();
        errorHandlers = new ErrorHandlers();
        clientOptimizationHandler = new ClientOptimizationHandler();
    }

    private void registerHandlers() {
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "RoomReady", roomEntryHandler::onRoomReady);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "GetGuestRoomResult", roomDetailsHandlers::onGetGuestRoomResult);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "RoomVisualizationSettings", roomDetailsHandlers::onRoomVisualizationSettings);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "Objects", itemPlacementHandlers::onFloorItems);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "Items", itemPlacementHandlers::onWallItems);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "ObjectAdd", itemAdditionHandlers::onObjectAdd);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "ItemAdd", itemAdditionHandlers::onItemAdd);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOSERVER,
                "MoveObject", furniMovementHandlers::onMoveFurni);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "ObjectUpdate", furniMovementHandlers::onMoveFurni);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOSERVER,
                "MoveWallItem", furniMovementHandlers::onMoveWallItem);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "ItemUpdate", furniMovementHandlers::onMoveWallItem);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "NavigatorSearchResultBlocks", navigatorHandlers::onNavigatorSearchResultBlocks);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "NewConsole", consoleHandlers::onNewConsole);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOSERVER,
                "SendMsg", consoleHandlers::onNewConsole);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "Users", userHandlers::onUsers);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "Chat", userHandlers::onChat);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "CantConnect", errorHandlers::onCantConnect);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "ErrorReport", errorHandlers::onErrorReport);

        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "Users", clientOptimizationHandler::onClientOptimization);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "RoomProperty", clientOptimizationHandler::onClientOptimization);
        HabboScanner.getInstance().intercept(HMessage.Direction.TOCLIENT,
                "HeightMap", clientOptimizationHandler::onClientOptimization);
    }
}

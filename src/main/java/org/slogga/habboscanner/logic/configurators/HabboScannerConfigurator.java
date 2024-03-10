package org.slogga.habboscanner.logic.configurators;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import lombok.Data;

import org.slogga.habboscanner.handlers.*;
import org.slogga.habboscanner.logic.DefaultValues;

@Data
public class HabboScannerConfigurator implements IConfigurator {
    private Map<String, Properties> properties = new HashMap<>();

    private RoomInfoHandlers roomInfoHandlers;
    private ItemProcessingHandlers itemProcessingHandlers;
    private FurniMovementHandlers furniMovementHandlers;
    private NavigatorHandlers navigatorHandlers;
    private ConsoleHandlers consoleHandlers;
    private UserHandlers userHandlers;
    private ErrorHandlers errorHandlers;

    public HabboScannerConfigurator() {
        setupConfig();
        setupHandlers();
    }

    private void setupHandlers() {
        this.roomInfoHandlers = new RoomInfoHandlers();
        this.itemProcessingHandlers = new ItemProcessingHandlers();
        this.furniMovementHandlers = new FurniMovementHandlers();
        this.navigatorHandlers = new NavigatorHandlers();
        this.consoleHandlers = new ConsoleHandlers();
        this.userHandlers = new UserHandlers();
        this.errorHandlers = new ErrorHandlers();
    }
    @Override
    public void setupConfig() {
        properties = new HashMap<>();
        DefaultValues.getInstance().getPropertyNames().forEach(this::loadProperty);
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
}

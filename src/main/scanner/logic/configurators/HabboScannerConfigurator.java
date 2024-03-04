package scanner.logic.configurators;

import lombok.Data;

import scanner.logic.IConfigurator;
import scanner.handlers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

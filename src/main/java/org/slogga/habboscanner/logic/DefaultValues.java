package org.slogga.habboscanner.logic;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slogga.habboscanner.HabboScanner;

import java.util.*;

@Data
public class DefaultValues {
    private static DefaultValues instance;

    private final Logger logger = LoggerFactory.getLogger(HabboScanner.class);
    private final List<String> propertyNames = Arrays.asList("bot", "message", "discord", "command_description");
    private final List<String> validDomains = Arrays.asList("s2", "it", "fi", "es", "de",
            "com.br", "com.tr", "com", "fr", "nl");
    public static DefaultValues getInstance() {
        if (instance == null)
            instance = new DefaultValues();

        return instance;
    }
}

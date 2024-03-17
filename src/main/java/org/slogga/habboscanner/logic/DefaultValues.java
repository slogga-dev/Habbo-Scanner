package org.slogga.habboscanner.logic;

import java.util.*;

import lombok.Data;

@Data
public class DefaultValues {
    private static DefaultValues instance;

    private final List<String> propertyNames = Arrays.asList("bot", "message", "discord", "command_description");
    private final List<String> validDomains = Arrays.asList("s2", "it", "fi", "es", "de",
            "com.br", "com.tr", "com", "fr", "nl");

    public static DefaultValues getInstance() {
        if (instance == null)
            instance = new DefaultValues();

        return instance;
    }
}

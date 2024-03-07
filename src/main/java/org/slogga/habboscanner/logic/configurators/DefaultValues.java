package org.slogga.habboscanner.logic.configurators;

import lombok.Data;

import java.util.*;

@Data
public class DefaultValues {
    private static DefaultValues instance;

    private List<String> propertyNames = Arrays.asList("bot", "message", "discord", "command_description");

    public static DefaultValues getInstance() {
        if (instance == null)
            instance = new DefaultValues();

        return instance;
    }
}

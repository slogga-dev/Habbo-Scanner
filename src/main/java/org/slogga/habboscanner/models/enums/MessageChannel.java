package org.slogga.habboscanner.models.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum MessageChannel {
    CHAT("chat"),
    CONSOLE("console");

    public static MessageChannel fromValue(String value) {
        return Arrays.stream(MessageChannel.values())
                .filter(mode -> Objects.equals(mode.getChannel(), value))
                .findFirst()
                .orElse(null);
    }

    private final String channel;

    MessageChannel(String channel) {
        this.channel = channel;
    }
}

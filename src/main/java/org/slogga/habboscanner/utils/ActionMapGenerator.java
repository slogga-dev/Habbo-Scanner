package org.slogga.habboscanner.utils;

import java.util.*;

import org.slogga.habboscanner.HabboScanner;

public class ActionMapGenerator {
    public static Map<Character, Runnable> generateActionMapFromValue(String value) {
        Map<Character, Runnable> actions = new HashMap<>();

        if (value.length() <= 2)
            return actions;

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String message = value.substring(2, value.length() - 1);

        actions.put('s', () -> HabboScanner.getInstance().sendAvatarExpression(1));
        actions.put('m', () -> HabboScanner.getInstance().sendPrivateMessage(consoleUserId, message));
        actions.put('b', () -> HabboScanner.getInstance().dance(1));

        try {
            int signId = Integer.parseInt(message);

            actions.put('c', () -> HabboScanner.getInstance().sign(signId));
        } catch (NumberFormatException exception) {
            return actions;
        }

        return actions;
    }
}

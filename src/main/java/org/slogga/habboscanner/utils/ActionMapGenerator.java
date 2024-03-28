package org.slogga.habboscanner.utils;

import java.util.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.HabboActions;

public class ActionMapGenerator {
    public static Map<Character, Runnable> generateActionMapFromValue(String value) {
        Map<Character, Runnable> actions = new HashMap<>();

        if (value.length() <= 2)
            return actions;

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String message = value.substring(2, value.length() - 1);

        actions.put('s', () -> HabboActions.sendAvatarExpression(1));
        actions.put('m', () -> HabboActions.sendMessage(consoleUserId, message));
        actions.put('b', () -> HabboActions.dance(1));

        try {
            int signId = Integer.parseInt(message);

            actions.put('c', () -> HabboActions.sign(signId));
        } catch (NumberFormatException exception) {
            return actions;
        }

        return actions;
    }
}

package org.slogga.habboscanner.logic.discord;

import java.io.IOException;

import okhttp3.*;

import org.slogga.habboscanner.HabboScanner;

public class DiscordWebhook {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static void sendDiscordEmbedMessage(String title, String description, int hex,
                                               String thumbnailUrl, String iconUrl, String footerText, String imageUrl) {
        boolean isDiscordWebhookEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("discord").getProperty("discord.webhook.enabled"));

        if (!isDiscordWebhookEnabled) return;

        String url = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("discord").getProperty("discord.webhook.url");

        String json = "{"
                + "\"embeds\": ["
                + "{"
                + "\"title\": \"" + title + "\","
                + "\"description\": \"" + description + "\","
                + "\"color\": " + hex + ","
                + "\"thumbnail\": {\"url\": \"" + thumbnailUrl + "\"},"
                + "\"footer\": {\"text\": \"" + footerText + "\", " +
                "\"icon_url\": \"" + iconUrl + "\"}";

        if (imageUrl != null)
            json += ",\"image\": {\"url\": \"" + imageUrl + "\"}";

        json += "}"
                + "]"
                + "}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

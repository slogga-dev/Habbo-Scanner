package scanner.utils;

import okhttp3.*;

import java.io.IOException;

public class JSONUtils {
    public static String fetchJSON(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) return "";

            return response.body().string();
        }
    }
}

package net.sand.logoutlives.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sand.logoutlives.LogoutLives;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SkinFetcher {
    private static final String SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    public static SkinData fetch(UUID uuid) throws Exception {
        String urlStr = String.format(SESSION_URL, uuid.toString().replace("-", ""));
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (Reader reader = new InputStreamReader(conn.getInputStream())) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray props = json.getAsJsonArray("properties");
            for (int i = 0; i < props.size(); i++) {
                JsonObject prop = props.get(i).getAsJsonObject();
                String name = prop.get("name").getAsString();
                if ("textures".equals(name)) {
                    String value = prop.get("value").getAsString();
                    String signature = prop.get("signature").getAsString();
                    return new SkinData(value, signature);
                }
            }
        }
        throw new IllegalStateException("No textures property found for uuid " + uuid);
    }

    // inner class
    public static class SkinData {
        public final String value;
        public final String signature;

        public SkinData(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }
    }
}

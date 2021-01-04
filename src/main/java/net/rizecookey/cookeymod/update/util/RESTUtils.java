package net.rizecookey.cookeymod.update.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class RESTUtils {
    private static final Logger LOGGER = LogManager.getLogger(RESTUtils.class.getSimpleName());

    public static JsonElement makeJsonGetRequest(String urlString, String... requestHeaders) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LOGGER.error("Invalid URL \"" + urlString + "\"!");
            LOGGER.error(e);
            return null;
        }
        try {
            LOGGER.info("Opening HTTPS connection to \"" + url.toString() + "\"...");

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            for (String property : requestHeaders) {
                String[] split = property.split(":");
                if (split.length % 2 != 0) {
                    LOGGER.error("Invalid connection requestHeaders! " + Arrays.toString(requestHeaders));
                    return null;
                }
                con.setRequestProperty(split[0], split[1].trim());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement json = gson.fromJson(reader, JsonElement.class);
                reader.close();
                con.disconnect();

                LOGGER.info("Done.");
                return json;
            }
            else {
                LOGGER.error("HTTPS connection returned response code " + responseCode + "!");
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to open connection to \"" + url.toString() + "\"!");
            LOGGER.error(e);
            return null;
        }
    }
}

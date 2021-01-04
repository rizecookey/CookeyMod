package net.rizecookey.cookeymod.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.rizecookey.cookeymod.update.util.RESTUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModUpdater {
    private static final Logger LOGGER = LogManager.getLogger(ModUpdater.class.getSimpleName());

    public static boolean checkForUpdates(String currentVersion) {
        JsonElement result = RESTUtils.makeJsonGetRequest(
                "https://api.github.com/repos/rizecookey/CookeyMod/releases?per_page=10&page=1",
                "Accept: application/vnd.github.v3+json");

        if (result != null) {
            JsonArray releases = result.getAsJsonArray();
            for (JsonElement release : releases) {
                JsonObject releaseObj = release.getAsJsonObject();
                try {
                    SemanticVersion releaseVersion = SemanticVersion.parse(releaseObj.get("tag_name").getAsString());
                    SemanticVersion currentModVersion = SemanticVersion.parse(currentVersion);
                    if (releaseVersion.compareTo(currentModVersion) > 0 && !releaseObj.get("target_commitish").getAsString().equals("vanilla-release")) {
                        return true;
                    }
                } catch (VersionParsingException e) {
                    LOGGER.error(e);
                }

            }
        }
        return false;
    }
}

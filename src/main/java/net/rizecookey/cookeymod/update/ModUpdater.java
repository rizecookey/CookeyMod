package net.rizecookey.cookeymod.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.rizecookey.cookeymod.update.util.RESTUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModUpdater {
    private static final Logger LOGGER = LogManager.getLogger(ModUpdater.class.getSimpleName());
    private static final String PREFIX = "[CookeyMod Updater] ";

    static Path updateDir;

    public static void update(String version, String repo, Path cache) {
        updateDir = cache;
        new Thread(() -> {
            LOGGER.info(PREFIX + "Checking for updates...");
            JsonElement result = RESTUtils.makeJsonGetRequest(
                    "https://api.github.com/repos/rizecookey/CookeyMod/releases?per_page=10&page=1",
                    "Accept: application/vnd.github.v3+json");

            if (result != null) {
                JsonArray releases = result.getAsJsonArray();
                for (JsonElement release : releases) {
                    JsonObject releaseObj = release.getAsJsonObject();
                    try {
                        SemanticVersion releaseVersion = SemanticVersion.parse(releaseObj.get("tag_name").getAsString());
                        SemanticVersion currentModVersion = SemanticVersion.parse(version);
                        String targetCommitish = releaseObj.get("target_commitish").getAsString();
                        if (releaseVersion.compareTo(currentModVersion) > 0 && targetCommitish.equals(repo)) {
                            cache.toFile().mkdirs();
                            LOGGER.info(PREFIX + "Found newer version: " + releaseVersion.getFriendlyString() + ", downloading...");
                            try {
                                ReadableByteChannel byteChannel = Channels.newChannel(new URL(
                                        releaseObj
                                                .get("assets").getAsJsonArray()
                                                .get(0).getAsJsonObject()
                                                .get("browser_download_url").getAsString()).openStream());
                                FileOutputStream out = new FileOutputStream(Paths.get(cache.toString(), "cookeymod-" + releaseVersion.getFriendlyString() + ".jar").toFile());
                                out.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
                                out.close();
                                byteChannel.close();
                                LOGGER.info(PREFIX + "Successfully downloaded newest version, restart to apply.");
                                return;
                            } catch (IOException e) {
                                LOGGER.error(e);
                            }
                        }
                    } catch (VersionParsingException e) {
                        LOGGER.error(e);
                    }
                }
                LOGGER.info(PREFIX + "Up to date.");
            }
        }).start();
    }

    public static void applyUpdate() {
        new Thread(() -> {
        }).start();
    }
}

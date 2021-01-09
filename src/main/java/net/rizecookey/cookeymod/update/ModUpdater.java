package net.rizecookey.cookeymod.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.rizecookey.cookeymod.update.util.RESTUtils;
import net.rizecookey.cookeymod.util.PrefixLogger;
import org.apache.logging.log4j.LogManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class ModUpdater {
    private static final PrefixLogger LOGGER = new PrefixLogger(LogManager.getLogger("CookeyMod Updater"));

    static Path updateDir;

    public static void update(String version, String repoBranch, Path updateDir) {
        ModUpdater.updateDir = updateDir;
        new Thread(() -> {
            LOGGER.info("Checking for updates...");
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
                        if (releaseVersion.compareTo(currentModVersion) > 0 && targetCommitish.equals(repoBranch)) {
                            Files.createDirectories(updateDir);
                            LOGGER.info("Found newer version: " + releaseVersion.getFriendlyString() + ", downloading...");
                            try {
                                ReadableByteChannel byteChannel = Channels.newChannel(new URL(
                                        releaseObj
                                                .get("assets").getAsJsonArray()
                                                .get(0).getAsJsonObject()
                                                .get("browser_download_url").getAsString()).openStream());
                                FileOutputStream out = new FileOutputStream(updateDir.resolve("cookeymod-" + releaseVersion.getFriendlyString() + ".jar").toFile());
                                out.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
                                out.close();
                                byteChannel.close();
                                LOGGER.info("Successfully downloaded newest version, restart to apply.");
                                return;
                            } catch (IOException e) {
                                LOGGER.unwrap().error(e);
                            }
                        }
                    } catch (VersionParsingException | IOException e) {
                        LOGGER.unwrap().error(e);
                    }
                }
                LOGGER.info("Up to date.");
            } else {
                LOGGER.info("Aborting.");
            }
        }, "CookeyMod Updater").start();
    }

    public static void applyUpdate(Path currentModFile, Path modsDir) {
        try {
            if (Files.exists(updateDir) && Files.list(updateDir).findFirst().isPresent()) {
                Optional<Path> modOpt;
                modOpt = Files.list(updateDir)
                        .filter((path -> path.getFileName().toString().endsWith(".jar")))
                        .findFirst();
                if (modOpt.isPresent()) {
                    LOGGER.info("Copying mod update to mod folder...");
                    Path mod = modOpt.get();
                    if (currentModFile != null && Files.exists(currentModFile)) {
                        Files.delete(currentModFile);
                    }
                    Files.copy(mod, modsDir.resolve(mod.getFileName()));
                    Files.walk(updateDir).sorted(Comparator.reverseOrder()).filter((Objects::nonNull)).forEach((path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            LOGGER.unwrap().error(e);
                        }
                    }));
                    LOGGER.info("Successfully copied.");
                }

            }
        } catch (IOException e) {
            LOGGER.unwrap().error(e);
        }
    }
}

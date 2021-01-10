package net.rizecookey.cookeymod.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.rizecookey.cookeymod.update.util.RESTUtils;
import net.rizecookey.cookeymod.util.PrefixLogger;
import org.apache.logging.log4j.LogManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class GitHubUpdater {
    PrefixLogger logger;
    FabricLoader loader;
    ModMetadata modMetadata;
    Path modsDir;
    Path updateDir;

    boolean updateDownloaded;

    public GitHubUpdater(ModMetadata modMetadata, Path modsDir) {
        loader = FabricLoader.getInstance();
        this.modMetadata = modMetadata;
        this.modsDir = modsDir;
        updateDir = loader.getConfigDir().resolve(modMetadata.getId()).resolve("update");
        logger = new PrefixLogger(LogManager.getLogger(modMetadata.getName() + " Updater"));
    }

    public void checkForUpdates(String user, String repo, String branch) {
        new Thread(() -> {
            updateDownloaded = false;
            logger.info("Checking for updates...");
            JsonElement result = RESTUtils.makeJsonGetRequest(
                    "https://api.github.com/repos/" + user + "/" + repo + "/releases?per_page=10&page=1",
                    "Accept: application/vnd.github.v3+json");

            if (result != null) {
                JsonArray releases = result.getAsJsonArray();
                for (JsonElement release : releases) {
                    JsonObject releaseObj = release.getAsJsonObject();
                    try {
                        SemanticVersion releaseVersion = SemanticVersion.parse(releaseObj.get("tag_name").getAsString());
                        SemanticVersion currentModVersion = SemanticVersion.parse(modMetadata.getVersion().getFriendlyString());
                        String targetCommitish = releaseObj.get("target_commitish").getAsString();
                        if (releaseVersion.compareTo(currentModVersion) > 0 && targetCommitish.equals(branch)) {
                            logger.info("Found newer version: " + releaseVersion.getFriendlyString() + ", downloading...");
                            downloadUpdate(new URL(releaseObj
                                    .get("assets").getAsJsonArray()
                                    .get(0).getAsJsonObject()
                                    .get("browser_download_url").getAsString()), releaseVersion.getFriendlyString());
                        }
                    } catch (VersionParsingException | IOException e) {
                        logger.unwrap().error(e);
                    }
                }
                logger.info("Up to date.");
            } else {
                logger.info("Aborting.");
            }
        }, logger.unwrap().getName()).start();
    }

    public void downloadUpdate(URL url, String version) {
        try {
            logger.info("Downloading newest version from " + url.toString() + "...");
            Files.createDirectories(updateDir);
            ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
            FileOutputStream out = new FileOutputStream(updateDir.resolve(modMetadata.getId() + "-" + version + ".jar").toFile());
            out.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
            out.close();
            byteChannel.close();
            logger.info("Successfully downloaded newest version, restart to apply.");
            this.updateDownloaded = true;
        } catch (IOException e) {
            logger.unwrap().error(e);
        }
    }

    public void applyUpdate() {
        try {
            if (updateDownloaded && Files.exists(updateDir) && Files.list(updateDir).findFirst().isPresent()) {
                Optional<Path> modOpt;
                modOpt = Files.list(updateDir)
                        .filter((path -> path.getFileName().toString().endsWith(".jar")))
                        .findFirst();
                if (modOpt.isPresent()) {
                    logger.info("Copying mod update to mod folder...");
                    Path mod = modOpt.get();
                    Path currentModFile = Paths.get(GitHubUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                    if (Files.exists(currentModFile)) {
                        Files.delete(currentModFile);
                    }
                    Files.copy(mod, modsDir.resolve(mod.getFileName()));
                    Files.walk(updateDir).sorted(Comparator.reverseOrder()).filter((Objects::nonNull)).forEach((path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            logger.unwrap().error(e);
                        }
                    }));
                    logger.info("Successfully copied.");
                }

            }
        } catch (IOException | URISyntaxException e) {
            logger.unwrap().error(e);
        }
    }
}

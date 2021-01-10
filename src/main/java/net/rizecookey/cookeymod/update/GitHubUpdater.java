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

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public boolean checkForUpdates(String user, String repo, String branch) {
        try {
            setUpdateDownloaded(false);
            logger.info("Checking for updates...");
            JsonElement result = RESTUtils.makeJsonGetRequest(
                    "https://api.github.com/repos/" + user + "/" + repo + "/releases?per_page=10&page=1",
                    "Accept: application/vnd.github.v3+json");

            if (result != null) {
                JsonArray releases = result.getAsJsonArray();
                for (JsonElement release : releases) {
                    JsonObject releaseObj = release.getAsJsonObject();
                    SemanticVersion releaseVersion = SemanticVersion.parse(releaseObj.get("tag_name").getAsString());
                    SemanticVersion currentModVersion = SemanticVersion.parse(modMetadata.getVersion().getFriendlyString());
                    String targetCommitish = releaseObj.get("target_commitish").getAsString();
                    if (releaseVersion.compareTo(currentModVersion) > 0 && targetCommitish.equals(branch)) {
                        logger.info("Found newer version: " + releaseVersion.getFriendlyString() + ", downloading...");
                        return downloadUpdate(new URL(releaseObj
                                .get("assets").getAsJsonArray()
                                .get(0).getAsJsonObject()
                                .get("browser_download_url").getAsString()), releaseVersion.getFriendlyString());
                    }
                }
                logger.info("Up to date.");
            } else {
                logger.info("Aborting.");
            }
        } catch (VersionParsingException | IOException e) {
            logger.unwrap().error(e);
        }
        return false;
    }

    public synchronized boolean downloadUpdate(URL url, String version) {
        try {
            logger.info("Downloading newest version from " + url.toString() + "...");
            Files.createDirectories(updateDir);
            Files.list(updateDir).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    logger.unwrap().error(e);
                }
            });
            BufferedInputStream in = new BufferedInputStream(url.openStream());
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(updateDir.resolve(modMetadata.getId() + "-" + version + ".jar").toFile()));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            logger.info("Successfully downloaded newest version, restart to apply.");
            setUpdateDownloaded(true);
            return true;
        } catch (IOException e) {
            logger.unwrap().error(e);
            return false;
        }
    }

    public void applyUpdate() {
        try {
            logger.info("Copying updater to temp directory...");
            Path currentFile = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path newFile = tempDir.resolve(currentFile.getFileName());
            Files.copy(currentFile, newFile, StandardCopyOption.REPLACE_EXISTING);

            String exec = "java" +
                    " -jar" +
                    " \"" + newFile.toAbsolutePath().toString() + "\"" +
                    " applyUpdate" +
                    " \"" + updateDir.toAbsolutePath().toString() + "\"" +
                    " \"" + currentFile.toAbsolutePath().toString() + "\"";
            logger.info("Executing updater with arguments as follows:");
            logger.info(exec);
            Runtime.getRuntime().exec(exec);
        } catch (URISyntaxException e) {
            logger.error("Unable to find current mod file!");
            logger.unwrap().error(e);
        } catch (IOException e) {
            logger.error("Unable to copy mod jar to temp folder for updating!");
            logger.unwrap().error(e);
        }
    }

    public synchronized void setUpdateDownloaded(boolean updated) {
        updateDownloaded = updated;
    }

    public synchronized boolean isUpdateDownloaded() {
        return updateDownloaded;
    }
}

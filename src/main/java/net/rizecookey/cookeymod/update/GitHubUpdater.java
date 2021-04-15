package net.rizecookey.cookeymod.update;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.rizecookey.cookeymod.update.util.RESTUtils;
import net.rizecookey.cookeymod.util.PrefixLogger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

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

    public boolean checkForUpdates(String user, String repo, String mcVersion) {
        setUpdateDownloaded(false);
        logger.info("Checking for updates...");

        String tag = null;

        try {
            JsonElement versionData = RESTUtils.makeJsonGetRequest(
                    "https://raw.githubusercontent.com/" + user + "/" + repo + "/versions/newest.json",
                    "Accept: application/vnd.github.v3+json");
            assert versionData != null;
            JsonObject versions = versionData.getAsJsonObject().get("minecraft").getAsJsonObject();
            SemanticVersion mcVersionSem = SemanticVersion.parse(mcVersion);
            for (String s : versions.keySet()) {
                if (SemanticVersion.parse(s).compareTo(mcVersionSem) <= 0) {
                    tag = versions.get(s).getAsJsonObject().get("release_tag").getAsString();
                    break;
                }
            }
            if (tag == null) {
                logger.warn("No version information for the current Minecraft version, aborting.");
                return false;
            }
        } catch (AssertionError | NullPointerException | IllegalStateException | VersionParsingException e) {
            logger.warn("Unable to download version info, aborting.");
            logger.unwrap().warn(e);
            return false;
        }

        try {
            SemanticVersion release = SemanticVersion.parse(tag);
            SemanticVersion current = SemanticVersion.parse(modMetadata.getVersion().getFriendlyString());
            if (release.compareTo(current) == 0) {
                logger.info("Up to date.");
                return false;
            }
            else if (release.compareTo(current) < 0) {
                logger.info("Using a newer version than the current release, interesting...");
                return false;
            }

        } catch (VersionParsingException e) {
            logger.warn("Unable to parse version, aborting.");
            logger.unwrap().warn(e);
        }

        try {
            JsonElement releaseData = RESTUtils.makeJsonGetRequest(
                    "https://api.github.com/repos/" + user + "/" + repo + "/releases/tags/" + tag,
                    "Accept: application/vnd.github.v3+json");

            assert releaseData != null;

            return downloadUpdate(new URL(
                    releaseData.getAsJsonObject()
                            .get("assets").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("browser_download_url").getAsString()
                    ), tag);
        } catch (AssertionError | NullPointerException | IllegalStateException | MalformedURLException e) {
            logger.warn("Unable to fetch release tag, aborting.");
            logger.unwrap().warn(e);
            return false;
        }
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

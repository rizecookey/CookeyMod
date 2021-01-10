package net.rizecookey.cookeymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.update.GitHubUpdater;
import net.rizecookey.cookeymod.util.PrefixLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Optional;

public class CookeyMod implements ModInitializer {
    ModContainer modContainer;
    ModMetadata modMetadata;
    ModConfig config;
    PrefixLogger logger = new PrefixLogger(LogManager.getLogger("CookeyMod"));

    GitHubUpdater updater;

    static CookeyMod instance;

    @Override
    public void onInitialize() {
        logger.info("Loading CookeyMod...");

        CookeyMod.instance = this;
        FabricLoader loader = FabricLoader.getInstance();

        Optional<ModContainer> opt = loader.getModContainer("cookeymod");
        if (opt.isPresent()) {
            modContainer = opt.get();
            modMetadata = modContainer.getMetadata();

            Path configDir = loader.getConfigDir().resolve(getModId());

            config = new ModConfig(configDir.resolve("config.toml"));
            updater = new GitHubUpdater(modMetadata, loader.getConfigDir().resolve("mods"));

            logger.info("CookeyMod " + getModVersion() + " has been loaded.");
        }
        else {
            logger.error("Couldn't find own mod container!");
            System.exit(-1);
        }

    }

    public Logger getLogger() {
        return this.logger.unwrap();
    }

    public ModConfig getConfig() {
        return this.config;
    }

    public String getModId() {
        return modMetadata.getId();
    }

    public String getModVersion() {
        return modMetadata.getVersion().getFriendlyString();
    }

    public ModContainer getModContainer() {
        return modContainer;
    }

    public GitHubUpdater getUpdater() {
        return updater;
    }

    public static CookeyMod getInstance() {
        return instance;
    }
}

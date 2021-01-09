package net.rizecookey.cookeymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.update.ModUpdater;
import net.rizecookey.cookeymod.util.PrefixLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class CookeyMod implements ModInitializer {
    public static final String MINECRAFT_VERSION = "1.16_combat-6";

    public Path updateDir;

    ModConfig config;
    PrefixLogger logger = new PrefixLogger(LogManager.getLogger("CookeyMod"));

    static CookeyMod instance;

    @Override
    public void onInitialize() {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        updateDir = fabricLoader.getConfigDir().resolve("cookeymod/config.toml");

        logger.info("Loading CookeyMod...");

        CookeyMod.instance = this;
        this.config = new ModConfig(fabricLoader.getConfigDir().resolve("cookeymod/config.toml").toFile());

        // Unfinished
        // ModUpdater.update(fabricLoader.getModContainer("cookeymod").get().getMetadata().getVersion().toString(), MINECRAFT_VERSION, fabricLoader.getConfigDir().resolve("cookeymod/update/"));

        logger.info("CookeyMod has been loaded.");
    }

    public Logger getLogger() {
        return this.logger.unwrap();
    }

    public ModConfig getConfig() {
        return this.config;
    }

    public static CookeyMod getInstance() {
        return instance;
    }
}

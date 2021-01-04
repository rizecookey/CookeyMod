package net.rizecookey.cookeymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.update.ModUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class CookeyMod implements ModInitializer {
    public static final String MINECRAFT_VERSION = "1.16_combat-6";

    public Path updateDir;
    public Path modFile;

    ModConfig config;
    Logger logger = LogManager.getLogger("CookeyMod");

    static CookeyMod instance;

    @Override
    public void onInitialize() {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        updateDir = fabricLoader.getConfigDir().resolve("cookeymod/config.toml");

        logger.info("[" + getClass().getSimpleName() + "] Loading CookeyMod...");

        CookeyMod.instance = this;
        this.config = new ModConfig(fabricLoader.getConfigDir().resolve("cookeymod/config.toml").toFile());

        ModUpdater.update(fabricLoader.getModContainer("cookeymod").get().getMetadata().getVersion().toString(), MINECRAFT_VERSION, fabricLoader.getConfigDir().resolve("cookeymod/update/"));

        logger.info("[" + getClass().getSimpleName() + "] CookeyMod has been loaded.");
    }

    public Logger getLogger() {
        return this.logger;
    }

    public ModConfig getConfig() {
        return this.config;
    }

    public static CookeyMod getInstance() {
        return instance;
    }
}

package net.rizecookey.cookeymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.keybind.Keybinds;
import net.rizecookey.cookeymod.util.PrefixLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Optional;

public class CookeyMod implements ModInitializer {
    private static final PrefixLogger LOGGER = new PrefixLogger(LogManager.getLogger("CookeyMod"));
    private static CookeyMod INSTANCE;

    private ModConfig config;
    private ModContainer modContainer;
    private ModMetadata modMetadata;
    private Keybinds keybinds;

    @Override
    public void onInitialize() {
        LOGGER.info("Loading CookeyMod...");

        INSTANCE = this;
        FabricLoader loader = FabricLoader.getInstance();

        Optional<ModContainer> opt = loader.getModContainer("cookeymod");
        if (opt.isPresent()) {
            modContainer = opt.get();
            modMetadata = modContainer.getMetadata();

            Path configDir = loader.getConfigDir().resolve(getModId());

            config = new ModConfig(this, configDir.resolve("config.toml"));

            keybinds = new Keybinds();

            LOGGER.info("CookeyMod " + getModVersion() + " has been loaded.");
        } else {
            LOGGER.error("Couldn't find own mod container!");
            System.exit(-1);
        }
    }

    public Logger getLogger() {
        return LOGGER.unwrap();
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

    public Keybinds getKeybinds() {
        return keybinds;
    }

    public static CookeyMod getInstance() {
        return INSTANCE;
    }
}

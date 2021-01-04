package net.rizecookey.cookeymod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.rizecookey.cookeymod.config.ModConfig;
import net.rizecookey.cookeymod.update.util.RESTUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class CookeyMod implements ModInitializer {
    ModConfig config;
    Logger logger = LogManager.getLogger("CookeyMod");

    static CookeyMod instance;

    @Override
    public void onInitialize() {
        logger.info("[" + getClass().getSimpleName() + "] Loading CookeyMod...");

        CookeyMod.instance = this;
        this.config = new ModConfig(FabricLoader.getInstance().getConfigDir().resolve("cookeymod/config.toml").toFile());

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

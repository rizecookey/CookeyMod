package net.rizecookey.cookeymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.rizecookey.cookeymod.command.CookeyModCommand;
import net.rizecookey.cookeymod.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CookeyMod implements ModInitializer {
	ModConfig config;
	Logger logger = LogManager.getLogger();

	static CookeyMod instance;

	@Override
	public void onInitialize() {
		logger.info("Loading CookeyMod...");

		CookeyMod.instance = this;
		this.config = new ModConfig(FabricLoader.getInstance().getConfigDir().resolve("cookeymod/config.toml").toFile());

		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
			CookeyModCommand.registerCommand(dispatcher);
		}));

		logger.info("CookeyMod has been loaded.");
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

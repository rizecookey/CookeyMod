package net.rizecookey.cookeymod.util;

import org.apache.logging.log4j.Logger;

public class PrefixLogger {
    Logger logger;
    String prefix;
    public PrefixLogger(Logger logger) {
        this.logger = logger;
        this.prefix = logger.getName();
    }

    public void info(String message) {
        logger.info("[" + prefix + "] " + message);
    }

    public void error(String message) {
        logger.error("[" + prefix + "] " + message);
    }

    public void warn(String message) {
        logger.warn("[" + prefix + "] " + message);
    }

    public Logger unwrap() {
        return logger;
    }
}

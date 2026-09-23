package net.rizecookey.cookeymod.config.persistence;

import net.rizecookey.cookeymod.config.ModConfig;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigSerialization {
    /**
     * @param config the config object to parse into
     * @param file   the file from which to parse the config
     * @return whether the config was updated during parsing
     */
    boolean parseInto(ModConfig config, Path file) throws IOException;

    /**
     * @param config the config object to serialize
     * @param file   the file to serialize the config into
     */
    void serializeTo(ModConfig config, Path file) throws IOException;
}

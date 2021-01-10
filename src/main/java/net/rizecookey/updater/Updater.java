package net.rizecookey.updater;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Updater {
    static List<String> log = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(500);
        if (args.length > 0 && "applyUpdate".equals(args[0])) {
            if (args.length != 3) {
                log("Invalid argument.");
                writeLog();
                return;
            }
            Path updateDir = Paths.get(args[1]);
            Path modFile = Paths.get(args[2]);

            Optional<Path> opt;
            try {
                log("Finding update file");
                if (Files.exists(updateDir) && (opt = Files.list(updateDir)
                        .filter(path -> path.getFileName().toString().endsWith(".jar"))
                        .findFirst())
                        .isPresent()) {
                    Path updateFile = opt.get();
                    log("Found " + updateFile.toAbsolutePath().toString());
                    if (Files.exists(modFile)) {
                        log("Deleting " + modFile.toAbsolutePath().toString());
                        Files.delete(modFile);
                    }
                    Path newModFile = modFile.getParent().resolve(updateFile.getFileName().toString());
                    log("Copying " + updateFile.toAbsolutePath().toString() + " to " + newModFile.toAbsolutePath().toString());
                    Files.copy(updateFile, newModFile);
                    log("Deleting " + updateDir.toAbsolutePath().toString());
                    Files.walk(updateDir).sorted(Comparator.reverseOrder()).forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    log("Done.");
                }
                else {
                    log("Failed to find update file, aborting");
                }
                writeLog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void log(String message) {
        System.out.println(message);
        log.add(message);
    }

    public static void writeLog() {
        try {
            Path thisJar = Paths.get(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Files.write(thisJar.getParent().resolve("cookeymod-updater.log"), log);
        } catch (URISyntaxException | IOException e) {
            if (e instanceof AccessDeniedException) return;
            e.printStackTrace();
        }
    }
}

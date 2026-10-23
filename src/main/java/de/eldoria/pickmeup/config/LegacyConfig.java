package de.eldoria.pickmeup.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Turns a {@code config.yml} written by PickMeUp 1.x into one {@link PickMeUpConfig} reads.
 * <p>
 * 1.x wrote Bukkit serialized sections, which carry a {@code ==:} type marker, and a top level {@code debug} level.
 * Dropping those lines leaves plain YAML with the section and field names {@link ConfigFile} uses. The original file
 * is kept as {@code config.v1.yml}.
 * <p>
 * The message files of 1.x use placeholders the current localizer no longer understands, and a key once written is
 * never rewritten. They are moved to {@code messages.v1}, so fresh files are written on start.
 */
public final class LegacyConfig {
    private static final Pattern LEGACY_LINE = Pattern.compile("^(\\s*==:.*|debug:.*)$");

    private LegacyConfig() {
    }

    /**
     * Converts the configuration in the data folder if it was written by 1.x.
     *
     * @param dataFolder the plugin's data folder
     * @param logger     where the conversion is reported
     * @return whether a configuration was converted
     * @throws IOException if the configuration can not be read or written
     */
    public static boolean migrate(Path dataFolder, Logger logger) throws IOException {
        Path config = dataFolder.resolve("config.yml");
        if (!Files.exists(config)) return false;
        List<String> lines = Files.readAllLines(config);
        if (lines.stream().noneMatch(line -> line.trim().startsWith("==:"))) return false;

        Files.copy(config, dataFolder.resolve("config.v1.yml"), StandardCopyOption.REPLACE_EXISTING);
        Files.write(config, convert(lines));
        logger.info("Converted the configuration of PickMeUp 1.x. The old file was saved as config.v1.yml.");

        Path messages = dataFolder.resolve("messages");
        if (Files.isDirectory(messages)) {
            Files.move(messages, dataFolder.resolve("messages.v1"), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Moved the message files of PickMeUp 1.x to messages.v1.");
        }
        return true;
    }

    /**
     * Removes the lines only 1.x configurations contain.
     *
     * @param lines lines of a 1.x configuration
     * @return the lines without type markers and debug level
     */
    static List<String> convert(List<String> lines) {
        return lines.stream().filter(line -> !LEGACY_LINE.matcher(line).matches()).toList();
    }
}

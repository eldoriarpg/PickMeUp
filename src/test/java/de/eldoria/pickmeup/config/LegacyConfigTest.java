/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup.config;

import dev.chojo.ocular.Configurations;
import dev.chojo.ocular.dataformats.YamlDataFormat;
import dev.chojo.ocular.key.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

class LegacyConfigTest {
    private static final List<String> LEGACY = List.of(
            "debug: INFO",
            "generalSettings:",
            "  ==: pickMeUpGeneralSettings",
            "  language: de_DE",
            "  updateCheck: false",
            "carrySettings:",
            "  ==: pickMeUpCarrySettings",
            "  throwForce: 3.5",
            "  allowStacking: true",
            "mobSettings:",
            "  ==: pickMeUpMobSettings",
            "  allowedMobs:",
            "  - BOAT",
            "  - MUSHROOM_COW",
            "  requirePermission: false",
            "  blacklist: false",
            "worldSettings:",
            "  ==: pickMeUpWorldSettings",
            "  restrictWorlds: false",
            "  blacklist: false",
            "  worlds:",
            "  - world");

    @Test
    public void removesTypeMarkersAndDebugLevel() {
        List<String> converted = LegacyConfig.convert(LEGACY);

        Assertions.assertTrue(converted.stream().noneMatch(line -> line.contains("==:") || line.startsWith("debug:")));
        Assertions.assertEquals(LEGACY.size() - 5, converted.size());
    }

    @Test
    public void legacyConfigLoadsAndKeepsItsValues(@TempDir Path dir) throws IOException {
        Files.write(dir.resolve("config.yml"), LEGACY);
        Files.createDirectories(dir.resolve("messages"));

        Assertions.assertTrue(LegacyConfig.migrate(dir, Logger.getAnonymousLogger()));
        Assertions.assertTrue(Files.exists(dir.resolve("config.v1.yml")));
        Assertions.assertTrue(Files.isDirectory(dir.resolve("messages.v1")));
        Assertions.assertFalse(LegacyConfig.migrate(dir, Logger.getAnonymousLogger()));

        var configurations = new Configurations<>(
                dir,
                Key.builder(Path.of("config.yml"), ConfigFile::new).build(),
                List.of(new YamlDataFormat()),
                getClass().getClassLoader(),
                null);
        ConfigFile config = configurations.main();

        Assertions.assertEquals("de_DE", config.generalSettings().language());
        Assertions.assertFalse(config.generalSettings().updates().notice());
        Assertions.assertEquals(3.5, config.carrySettings().throwForce());
        Assertions.assertTrue(config.carrySettings().allowStacking());

        configurations.save();
        String saved = Files.readString(dir.resolve("config.yml"));
        Assertions.assertTrue(saved.contains("MUSHROOM_COW"));
        Assertions.assertTrue(saved.contains("notice: false"));
        Assertions.assertFalse(saved.contains("updateCheck"));
    }
}

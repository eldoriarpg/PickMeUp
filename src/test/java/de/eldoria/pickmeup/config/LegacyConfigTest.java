package de.eldoria.pickmeup.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class LegacyConfigTest {
    @Test
    public void removesTypeMarkersAndDebugLevel() {
        List<String> legacy = List.of(
                "debug: INFO",
                "generalSettings:",
                "  ==: pickMeUpGeneralSettings",
                "  language: en_US",
                "  updateCheck: true",
                "mobSettings:",
                "  ==: pickMeUpMobSettings",
                "  allowedMobs:",
                "  - BOAT");

        Assertions.assertEquals(List.of(
                "generalSettings:",
                "  language: en_US",
                "  updateCheck: true",
                "mobSettings:",
                "  allowedMobs:",
                "  - BOAT"), LegacyConfig.convert(legacy));
    }
}

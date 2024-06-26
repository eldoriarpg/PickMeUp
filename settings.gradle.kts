rootProject.name = "PickMeUp"

pluginManagement{
    repositories{
        gradlePluginPortal()
        maven{
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // utilities
            version("eldoutil", "2.0.10")
            library("legacy-serialization", "de.eldoria.util","legacy-serialization").versionRef("eldoutil")
            library("plugin", "de.eldoria.util","plugin").versionRef("eldoutil")
            library("metrics", "de.eldoria.util","metrics").versionRef("eldoutil")
            library("threading", "de.eldoria.util","threading").versionRef("eldoutil")
            library("updater", "de.eldoria.util","updater").versionRef("eldoutil")
            library("crossversion", "de.eldoria.util","crossversion").versionRef("eldoutil")
            bundle("eldoria-utilities", listOf("legacy-serialization", "plugin", "threading", "updater", "crossversion", "metrics"))

            // misc
            library("jetbrains-annotations", "org.jetbrains:annotations:24.1.0")
            // minecraft
            version("minecraft-latest", "1.20.1-R0.1-SNAPSHOT")
            library("paper-latest", "io.papermc.paper", "paper-api").versionRef("minecraft-latest")
            library("spigot-latest", "org.spigotmc", "spigot-api").versionRef("minecraft-latest")
            bundle("minecraft-latest", listOf("paper-latest", "spigot-latest"))

            library("paper-v17", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            library("spigot-v16", "org.spigotmc:spigot-api:1.16.1-R0.1-SNAPSHOT")

            // plugins
            plugin("publishdata", "de.chojo.publishdata").version("1.2.5")
            plugin("spotless", "com.diffplug.spotless").version("6.25.0")
            plugin("shadow", "io.github.goooler.shadow").version("8.1.8")
            plugin("pluginyml-bukkit", "net.minecrell.plugin-yml.bukkit").version("0.6.0")
        }

        create("testlibs") {
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
        }
    }
}

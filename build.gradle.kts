import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.TRUE

plugins {
    java
    `maven-publish`
    alias(libs.plugins.spotless)
    alias(libs.plugins.publishdata)
    alias(libs.plugins.publishdata.manifest)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://raw.githubusercontent.com/FabioZumbi12/RedProtect/mvn-repo/")
    maven("https://raw.githubusercontent.com/FabioZumbi12/UltimateChat/mvn-repo/")
    maven("https://jitpack.io")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://maven.enginehub.org/repo/")
    exclusiveContent {
        forRepository {
            maven("https://repo.codemc.org/repository/maven-public/") {
                metadataSources { artifact() }
            }
        }
        filter { includeModule("world.bentobox", "bentobox") }
    }
}

val shadebase = "de.eldoria.pickmeup.libs."

dependencies {
    implementation(libs.bundles.utilities) {
        exclude("com.fasterxml")
        exclude("net.kyori")
        exclude("org.jetbrains")
        exclude("org.yaml")
    }
    implementation(platform(libs.jackson.bom))
    implementation(libs.bundles.jackson)
    implementation(libs.bstats)

    compileOnly(libs.paper)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.bentobox) { isTransitive = false }
    compileOnly(libs.griefprevention) { isTransitive = false }
    compileOnly(libs.towny) { isTransitive = false }
    compileOnly(libs.plotsquared.core) { isTransitive = false }
    compileOnly(libs.plotsquared.bukkit) { isTransitive = false }
    compileOnly(libs.guice) { isTransitive = false }
    compileOnly(libs.worldguard.bukkit) {
        exclude("org.bukkit")
        exclude("org.spigotmc")
        exclude("io.papermc")
    }
    compileOnly(libs.redprotect.core) { isTransitive = false }
    compileOnly(libs.redprotect.spigot) { isTransitive = false }

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.paper)
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

spotless {
    java {
        target("src/**/*.java")
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        trimTrailingWhitespace()
        endWithNewline()
        palantirJavaFormat(libs.versions.palantir.get()).formatJavadoc(false)
        removeUnusedImports()
        importOrder("", "java", "javax", "\\#")
        encoding("UTF-8")
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            url = "https://github.com/eldoriarpg/PickMeUp"
            licenses {
                license {
                    name = "AGPL-3.0-only"
                    url = "https://github.com/eldoriarpg/PickMeUp/blob/master/LICENSE.md"
                }
            }
            developers {
                developer {
                    name = "Eldoria"
                }
            }
        }
    }
}

publishData {
    eldoNexus(dev = false)
    component = "shadow"
    buildData {
        put("lynaProduct", "5")
    }
}

pluginManifest {
    platforms("paper", "folia")
    optional("BentoBox") { spigot(73261) }
    optional("GriefPrevention") { spigot(1884) }
    optional("PlotSquared") { spigot(77506) }
    optional("Towny") { spigot(72694) }
    optional("RedProtect") { spigot(15841) }
    optional("Citizens") { spigot(13811) }
    optional("WorldGuard") { modrinth("worldguard") }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        relocate("org.bstats", shadebase + "bstats")
        relocate("com.fasterxml", shadebase + "fasterxml")
        relocate("tools.jackson", shadebase + "jackson")
        relocate("org.snakeyaml", shadebase + "snakeyaml")
        relocate("dev.chojo.ocular", shadebase + "ocular")
        relocate("de.eldoria.jacksonbukkit", shadebase + "jacksonbukkit")
        relocate("de.eldoria.eldoutilities", shadebase + "utilities")
        mergeServiceFiles()
        addMultiReleaseAttribute = false
        exclude("META-INF/versions/**")
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("26.3")
        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}

runPaper {
    folia {
        registerTask {
            minecraftVersion("26.3")
            jvmArgs("-Dcom.mojang.eula.agree=true")
        }
    }
}

bukkit {
    name = "PickMeUp"
    version = project.version.toString()
    description = "Pick up entities, carry them around and throw them away"
    authors = listOf("RainbowDashLabs")
    main = "de.eldoria.pickmeup.PickMeUp"
    website = "https://hangar.papermc.io/eldoriarpg/PickMeUp"
    apiVersion = "1.21.11"
    foliaSupported = true
    softDepend = listOf("BentoBox", "RedProtect", "GriefPrevention", "PlotSquared", "Towny", "Citizens", "WorldGuard")

    commands {
        register("pickmeup") {
            description = "Main command of pick me up"
            usage = "Trust the tab completion"
            aliases = listOf("pmu")
        }
    }

    permissions {
        register("pickmeup.*") {
            description = "All permissions of pick me up"
            children = listOf(
                "pickmeup.reload",
                "pickmeup.updates",
                "pickmeup.toggle",
                "pickmeup.bypass.nostack",
                "pickmeup.bypass.maxstack",
                "pickmeup.bypass.toggle")
        }
        register("pickmeup.reload") {
            description = "Reload the configuration and use the debug command"
            default = OP
        }
        register("pickmeup.updates") {
            description = "Hear about new versions on join and download them"
            default = OP
        }
        register("pickmeup.toggle") {
            description = "Decide whether other players may pick you up"
            default = TRUE
        }
        register("pickmeup.bypass.nostack") {
            description = "Pick up entities which carry entities themselves, even when stacking is disabled"
            default = OP
        }
        register("pickmeup.bypass.maxstack") {
            description = "Stack carried entities higher than the configured maximum"
            default = OP
        }
        register("pickmeup.bypass.toggle") {
            description = "Pick up players who disabled being picked up"
            default = OP
        }
    }
}

hangarPublish {
    publications.register("plugin") {
        version = project.version.toString()
        id = "PickMeUp"
        channel = System.getenv("HANGAR_CHANNEL") ?: "Release"
        apiKey = System.getenv("HANGAR_KEY")

        platforms {
            register(Platforms.PAPER) {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf("1.21.11-26.3")
            }
        }
    }
}

modrinth {
    token = providers.environmentVariable("MODRINTH_TOKEN")
    projectId = "eLiWuFr8"
    versionNumber = project.version.toString()
    versionType = "release"
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll("1.21.11", "26.1", "26.1.1", "26.1.2", "26.2", "26.3")
    loaders.addAll("paper", "purpur", "folia")
    changelog = providers.environmentVariable("MODRINTH_CHANGELOG").orElse("")
    syncBodyFrom = rootProject.file("README.md").readText()
}

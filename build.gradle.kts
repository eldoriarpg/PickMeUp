plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
    `maven-publish`
    id("de.chojo.publishdata") version "1.4.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
    maven("https://raw.githubusercontent.com/FabioZumbi12/RedProtect/mvn-repo/")
    maven("https://raw.githubusercontent.com/FabioZumbi12/UltimateChat/mvn-repo/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://jitpack.io")
    maven("https://repo.glaremasters.me/repository/towny/")
}

dependencies {
    bukkitLibrary(libs.bundles.eldoria.utilities)
    implementation(libs.bstats)
    compileOnly(libs.spigot.v16)
    compileOnly("com.mojang", "authlib", "1.5.25")
    compileOnly(libs.jetbrains.annotations)
    compileOnly("net.citizensnpcs", "citizens-main","2.0.30-SNAPSHOT") {
        exclude("*")
    }
    compileOnly("world.bentobox", "bentobox", "2.3.0-SNAPSHOT")
    compileOnly("com.github.TechFortress", "GriefPrevention", "17.0.0")
    compileOnly("com.palmergames.bukkit.towny", "towny", "0.100.4.4")
    compileOnly("com.plotsquared", "PlotSquared-Core", "6.11.2") {
        exclude("*")
    }
    compileOnly("com.plotsquared", "PlotSquared-Bukkit", "6.11.2") { isTransitive = false } // PlotSquared Bukkit API

    compileOnly("io.github.fabiozumbi12.RedProtect", "RedProtect-Core", "8.1.2") {
        exclude("*")
    }
    compileOnly("io.github.fabiozumbi12.RedProtect", "RedProtect-Spigot", "8.1.1") {
        exclude("com.github.MilkBowl")
        exclude("com.github.TheBusyBiscuit")
        exclude("com.gmail.nossr50.mcMMO")
        exclude("net.ess3")
        exclude("org.spigotmc")
        exclude("org.spongepowered")
        exclude("com.typesafe")
    }

    testImplementation(libs.bundles.eldoria.utilities)
    testImplementation(platform("org.junit:junit-bom:5.11.2"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk", "MockBukkit-v1.16", "1.5.2")
}

group = "de.eldoria"
version = "1.3.12"
var mainPackage = "pickmeup"
val shadebase = group as String? + "." + mainPackage + "."

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishData {
    useEldoNexusRepos()
    publishComponent("java")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            name = "EldoNexus"
            url = uri(publishData.getRepository())
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        relocate("org.bstats", shadebase + "bstats")
        mergeServiceFiles()
        minimize()
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: "";
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }

    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    authors = listOf("RainbowDashLabs")
    main = "de.eldoria.pickmeup.PickMeUp"
    website = "https://www.spigotmc.org/resources/88151/"
    apiVersion = "1.13"
    softDepend = listOf("BentoBox", "RedProtect", "GriefPrevention", "PlotSquared", "Towny", "Citizens")
    commands {
        register("pickmeup") {
            description = "Main command of pick me up"
            usage = "Trust the tab completion"
            aliases = listOf("pmu")
        }
    }
}

/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.pickmeup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Lets Paper download the libraries of PickMeUp before the plugin loads, instead of shipping them in the jar.
 * <p>
 * The libraries are the ones the build lists in {@value #LIBRARIES}. They are resolved from EldoNexus, which hosts
 * eldo-util, and from Paper's Maven Central mirror for everything else. Paper keeps them in its {@code libraries}
 * folder and gives them to this plugin only, so other plugins with other versions are not affected.
 */
public class PickMeUpLoader implements PluginLoader {
    private static final String LIBRARIES = "paper-libraries.json";
    private static final String ELDONEXUS = "https://eldonexus.de/repository/maven-public/";

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(repository("eldonexus", ELDONEXUS));
        resolver.addRepository(repository("central", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR));
        for (var library : libraries().getAsJsonArray("dependencies")) {
            resolver.addDependency(new Dependency(new DefaultArtifact(library.getAsString()), null));
        }
        classpathBuilder.addLibrary(resolver);
    }

    private static RemoteRepository repository(String id, String url) {
        return new RemoteRepository.Builder(id, "default", url).build();
    }

    private JsonObject libraries() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(LIBRARIES)) {
            if (in == null) throw new IllegalStateException(LIBRARIES + " is missing from the plugin jar");
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + LIBRARIES, e);
        }
    }
}

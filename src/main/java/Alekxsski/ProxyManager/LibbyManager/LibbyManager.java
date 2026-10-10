package Alekxsski.ProxyManager.LibbyManager;


import Alekxsski.Velcord;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.VelocityLibraryManager;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;

import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;

public class LibbyManager {

    private final Logger logger;
    private final Path dataDirectory;
    private final PluginManager pluginManager;

    @Inject
    public LibbyManager(Logger logger, @DataDirectory Path dataDirectory, PluginManager pluginManager){

        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.pluginManager = pluginManager;

    }

    public void loadLibby(Velcord plugin){

        VelocityLibraryManager<Velcord> velocityLibraryManager = new VelocityLibraryManager<>(plugin,logger,dataDirectory,pluginManager);
        velocityLibraryManager.addMavenCentral();
        velocityLibraryManager.addSonatype();

        List<Library> Libraries = List.of(

                Library.builder()
                .groupId("com.mysql")
                .artifactId("mysql-connector-j")
                .version("26.7.0")
                .build(),

                Library.builder()
                        .groupId("org.mariadb.jdbc")
                        .artifactId("mariadb-java-client")
                        .version("3.5.10")
                        .build(),

                Library.builder()
                        .groupId("org.xerial")
                        .artifactId("sqlite-jdbc")
                        .version("3.53.4.0")
                        .build(),

                Library.builder()
                        .groupId("io{}javalin")
                        .artifactId("javalin")
                        .version("7.2.3")
                        .relocate("io{}javalin", "com{}Alexsski{}libs{}javalin")
                        .relocate("org{}eclipse{}jetty", "com{}Alexsski{}libs{}jetty")
                        .resolveTransitiveDependencies(true)
                        .build(),

                Library.builder()
                        .groupId("net{}dv8tion")
                        .artifactId("JDA")
                        .version("6.6.0")
                        .resolveTransitiveDependencies(true)
                        .excludeTransitiveDependency("club.minnced","opus-java")
                        .excludeTransitiveDependency("com.google.crypto.tink","tink")
                        .build()

                );

        Libraries.forEach(velocityLibraryManager::loadLibrary);


    }


}
